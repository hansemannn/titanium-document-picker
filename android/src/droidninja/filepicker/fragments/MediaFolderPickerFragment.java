package droidninja.filepicker.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.RequestManager;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.adapters.FolderGridAdapter;
import droidninja.filepicker.adapters.FolderGridAdapter.FolderGridAdapterListener;
import droidninja.filepicker.cursors.loadercallbacks.FileResultCallback;
import droidninja.filepicker.models.Media;
import droidninja.filepicker.models.PhotoDirectory;
import droidninja.filepicker.utils.AndroidLifecycleUtils;
import droidninja.filepicker.utils.GridSpacingItemDecoration;
import droidninja.filepicker.utils.ImageCaptureManager;
import droidninja.filepicker.utils.MediaStoreHelper;
import ti.filepicker.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MediaFolderPickerFragment extends BaseFragment implements FolderGridAdapter.FolderGridAdapterListener
{
  private static final String TAG = MediaFolderPickerFragment.class.getSimpleName();

  private static final int SCROLL_THRESHOLD = 30;

  private static final int PERMISSION_WRITE_EXTERNAL_STORAGE_RC = 908;

  RecyclerView recyclerView;

  TextView emptyView;

  private PhotoPickerFragmentListener mListener;
  private FolderGridAdapter photoGridAdapter;
  private ImageCaptureManager imageCaptureManager;
  private RequestManager mGlideRequestManager;
  private int fileType;

  public MediaFolderPickerFragment() {}

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    return inflater.inflate(Utils.getR("layout.fragment_media_folder_picker"), container, false);
  }

  public void onAttach(Context context) {
    super.onAttach(context);
    if ((context instanceof PhotoPickerFragmentListener)) {
      mListener = ((PhotoPickerFragmentListener)context);
    }
    else {
      throw new RuntimeException(context.toString() + " must implement PhotoPickerFragmentListener");
    }
  }

  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public static MediaFolderPickerFragment newInstance(int fileType) {
    MediaFolderPickerFragment photoPickerFragment = new MediaFolderPickerFragment();
    Bundle bun = new Bundle();
    bun.putInt("FILE_TYPE", fileType);
    photoPickerFragment.setArguments(bun);
    return photoPickerFragment;
  }

  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mGlideRequestManager = com.bumptech.glide.Glide.with(this);
  }

  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initView(view);
  }

  private void initView(View view) {
    recyclerView = ((RecyclerView)view.findViewById(Utils.getR("id.recyclerview")));
    emptyView = ((TextView)view.findViewById(Utils.getR("id.empty_view")));
    fileType = getArguments().getInt("FILE_TYPE");

    imageCaptureManager = new ImageCaptureManager(getActivity());
    GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);

    int spanCount = 2;
    int spacing = 5;
    boolean includeEdge = false;
    recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (Math.abs(dy) > 30) {
          mGlideRequestManager.pauseRequests();
        } else {
          MediaFolderPickerFragment.this.resumeRequestsIfNotDestroyed();
        }
      }

      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == 0) {
          MediaFolderPickerFragment.this.resumeRequestsIfNotDestroyed();
        }
      }
    });
  }

  public void onResume() {
    super.onResume();
    getDataFromMedia();
  }

  private void getDataFromMedia() {
    Bundle mediaStoreArgs = new Bundle();
    mediaStoreArgs.putBoolean("SHOW_GIF",
      PickerManager.getInstance().isShowGif());
    mediaStoreArgs.putInt("EXTRA_FILE_TYPE", fileType);

    if (fileType == 1) {
      MediaStoreHelper.getPhotoDirs(getActivity(), mediaStoreArgs, new FileResultCallback<PhotoDirectory>()
      {
        public void onResultCallback(List<PhotoDirectory> dirs) {
          MediaFolderPickerFragment.this.updateList(dirs);
        }
      });
    } else if (fileType == 3) {
      MediaStoreHelper.getVideoDirs(getActivity(), mediaStoreArgs, new FileResultCallback<PhotoDirectory>()
      {
        public void onResultCallback(List<PhotoDirectory> dirs) {
          MediaFolderPickerFragment.this.updateList(dirs);
        }
      });
    }
  }

  private void updateList(List<PhotoDirectory> dirs) {
    android.util.Log.i("updateList", "" + dirs.size());
    if (dirs.size() > 0) {
      emptyView.setVisibility(8);
      recyclerView.setVisibility(0);
    } else {
      emptyView.setVisibility(0);
      recyclerView.setVisibility(8);
      return;
    }

    PhotoDirectory photoDirectory = new PhotoDirectory();
    photoDirectory.setBucketId("ALL_PHOTOS_BUCKET_ID");

    if (fileType == 3) {
      photoDirectory.setName(getString(Utils.getR("string.all_videos")));
    } else if (fileType == 1) {
      photoDirectory.setName(getString(Utils.getR("string.all_photos")));
    } else {
      photoDirectory.setName(getString(Utils.getR("string.all_files")));
    }

    if ((dirs.size() > 0) && (((PhotoDirectory)dirs.get(0)).getMedias().size() > 0)) {
      photoDirectory.setDateAdded(((PhotoDirectory)dirs.get(0)).getDateAdded());
      photoDirectory.setCoverPath(((Media)((PhotoDirectory)dirs.get(0)).getMedias().get(0)).getPath());
    }

    for (int i = 0; i < dirs.size(); i++) {
      photoDirectory.addPhotos(((PhotoDirectory)dirs.get(i)).getMedias());
    }

    dirs.add(0, photoDirectory);

    if (photoGridAdapter != null) {
      photoGridAdapter.setData(dirs);
      photoGridAdapter.notifyDataSetChanged();
    } else {
      if (fileType == 1) {}


      photoGridAdapter = new FolderGridAdapter(getActivity(), mGlideRequestManager, (ArrayList)dirs, null, PickerManager.getInstance().isEnableCamera());
      recyclerView.setAdapter(photoGridAdapter);

      photoGridAdapter.setFolderGridAdapterListener(this);
    }
  }

  public void onCameraClicked() {
    try {
      Intent intent = imageCaptureManager.dispatchTakePictureIntent(getActivity());
      if (intent != null) {
        startActivityForResult(intent, 257);
      } else {
        Toast.makeText(getActivity(), Utils.getR("string.no_camera_exists"), 0).show();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void onFolderClicked(PhotoDirectory photoDirectory) {
    Intent intent = new Intent(getActivity(), droidninja.filepicker.MediaDetailsActivity.class);
    intent.putExtra(PhotoDirectory.class.getSimpleName(), photoDirectory);
    intent.putExtra("EXTRA_FILE_TYPE", fileType);
    getActivity().startActivityForResult(intent, 235);
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
    case 257:
      if (resultCode == -1) {
        String imagePath = imageCaptureManager.notifyMediaStoreDatabase();
        if ((imagePath != null) && (PickerManager.getInstance().getMaxCount() == 1)) {
          PickerManager.getInstance().add(imagePath, 1);
          mListener.onItemSelected();
        } else {
          new Handler().postDelayed(new Runnable()
          {
            public void run() { MediaFolderPickerFragment.this.getDataFromMedia(); } }, 1000L);
        }
      }
      break;
    }

  }

  private void resumeRequestsIfNotDestroyed()
  {
    if (!AndroidLifecycleUtils.canLoadImage(this)) {
      return;
    }

    mGlideRequestManager.resumeRequests();
  }
}
