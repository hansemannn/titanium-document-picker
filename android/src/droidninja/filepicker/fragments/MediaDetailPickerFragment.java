package droidninja.filepicker.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.adapters.FileAdapterListener;
import droidninja.filepicker.adapters.PhotoGridAdapter;
import droidninja.filepicker.cursors.loadercallbacks.FileResultCallback;
import droidninja.filepicker.models.Media;
import droidninja.filepicker.models.PhotoDirectory;
import droidninja.filepicker.utils.AndroidLifecycleUtils;
import droidninja.filepicker.utils.ImageCaptureManager;
import droidninja.filepicker.utils.MediaStoreHelper;
import ti.filepicker.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MediaDetailPickerFragment
  extends BaseFragment implements FileAdapterListener
{
  private static final String TAG = MediaDetailPickerFragment.class.getSimpleName();

  private static final int SCROLL_THRESHOLD = 30;

  RecyclerView recyclerView;

  TextView emptyView;

  private PhotoPickerFragmentListener mListener;

  private PhotoGridAdapter photoGridAdapter;

  private ImageCaptureManager imageCaptureManager;

  private RequestManager mGlideRequestManager;
  private int fileType;
  private MenuItem selectAllItem;

  public MediaDetailPickerFragment() {}

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    return inflater.inflate(Utils.getR("layout.fragment_photo_picker"), container, false);
  }

  public void onAttach(Context context)
  {
    super.onAttach(context);
    if ((context instanceof PhotoPickerFragmentListener)) {
      mListener = ((PhotoPickerFragmentListener)context);
    } else {
      throw new RuntimeException(context.toString() + " must implement PhotoPickerFragmentListener");
    }
  }


  public void onDetach()
  {
    super.onDetach();
    mListener = null;
  }

  public static MediaDetailPickerFragment newInstance(int fileType) {
    MediaDetailPickerFragment mediaDetailPickerFragment = new MediaDetailPickerFragment();
    Bundle bun = new Bundle();
    bun.putInt("FILE_TYPE", fileType);
    mediaDetailPickerFragment.setArguments(bun);
    return mediaDetailPickerFragment;
  }

  public void onItemSelected()
  {
    mListener.onItemSelected();
    if ((photoGridAdapter != null) && (selectAllItem != null) &&
      (photoGridAdapter.getItemCount() == photoGridAdapter.getSelectedItemCount())) {
      selectAllItem.setIcon(Utils.getR("drawable.ic_select_all"));
      selectAllItem.setChecked(true);
    }
  }


  public void onCreate(@Nullable Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(PickerManager.getInstance().hasSelectAll());
    mGlideRequestManager = Glide.with(this);
  }

  public void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);
  }

  public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);
    initView(view);
  }

  private void initView(View view) {
    recyclerView = ((RecyclerView)view.findViewById(Utils.getR("id.recyclerview")));
    emptyView = ((TextView)view.findViewById(Utils.getR("id.empty_view")));
    fileType = getArguments().getInt("FILE_TYPE");
    imageCaptureManager = new ImageCaptureManager(getActivity());
    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, 1);
    layoutManager.setGapStrategy(2);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (Math.abs(dy) > 30) {
          mGlideRequestManager.pauseRequests();
        } else
          MediaDetailPickerFragment.this.resumeRequestsIfNotDestroyed();
      }

      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == 0) {
          MediaDetailPickerFragment.this.resumeRequestsIfNotDestroyed();
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

    mediaStoreArgs.putInt("EXTRA_FILE_TYPE", fileType);

    if (fileType == 1) {
      MediaStoreHelper.getPhotoDirs(getActivity(), mediaStoreArgs, new FileResultCallback<PhotoDirectory>()
      {
        public void onResultCallback(List<PhotoDirectory> dirs)
        {
          MediaDetailPickerFragment.this.updateList(dirs);
        }

      });
    } else if (fileType == 3)
    {
      MediaStoreHelper.getVideoDirs(getActivity(), mediaStoreArgs, new FileResultCallback<PhotoDirectory>()
      {
        public void onResultCallback(List<PhotoDirectory> dirs)
        {
          MediaDetailPickerFragment.this.updateList(dirs);
        }
      });
    }
  }

  private void updateList(List<PhotoDirectory> dirs) {
    ArrayList<Media> medias = new ArrayList();
    for (int i = 0; i < dirs.size(); i++) {
      medias.addAll(((PhotoDirectory)dirs.get(i)).getMedias());
    }

    Collections.sort(medias, new Comparator<Object>()
    {
      public int compare(Object a, Object b) {
        return ((Media) b).getId() - ((Media) a).getId();
      }
    });

    if (medias.size() > 0) {
      emptyView.setVisibility(8);
    }
    else {
      emptyView.setVisibility(0);
    }

    if (photoGridAdapter != null)
    {
      photoGridAdapter.setData(medias);
      photoGridAdapter.notifyDataSetChanged();
    }
    else
    {
      photoGridAdapter = new PhotoGridAdapter(getActivity(), mGlideRequestManager, medias, PickerManager.getInstance().getSelectedPhotos(), (fileType == 1) && (PickerManager.getInstance().isEnableCamera()), this);
      recyclerView.setAdapter(photoGridAdapter);
      photoGridAdapter.setCameraListener(new View.OnClickListener()
      {
        public void onClick(View v) {
          try {
            Intent intent = imageCaptureManager.dispatchTakePictureIntent(getActivity());
            if (intent != null) {
              startActivityForResult(intent, 257);
            } else
              Toast.makeText(getActivity(), Utils.getR("string.no_camera_exists"), 0).show();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      });
    }
  }


  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode)
    {
    case 257:
      if (resultCode == -1)
      {
        String imagePath = imageCaptureManager.notifyMediaStoreDatabase();
        if ((imagePath != null) && (PickerManager.getInstance().getMaxCount() == 1))
        {
          PickerManager.getInstance().add(imagePath, 1);
          mListener.onItemSelected();
        }
        else {
          new Handler().postDelayed(new Runnable()
          {

            public void run() { MediaDetailPickerFragment.this.getDataFromMedia(); } }, 1000L);
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

  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(Utils.getR("menu.select_menu"), menu);
    selectAllItem = menu.findItem(Utils.getR("id.action_select"));
    onItemSelected();
    super.onCreateOptionsMenu(menu, inflater);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == Utils.getR("id.action_select")) {
      if (photoGridAdapter != null) {
        photoGridAdapter.selectAll();
        if (selectAllItem != null)
          if (selectAllItem.isChecked()) {
            PickerManager.getInstance().clearSelections();
            photoGridAdapter.clearSelection();

            selectAllItem.setIcon(Utils.getR("drawable.ic_deselect_all"));
          }
          else {
            photoGridAdapter.selectAll();
            PickerManager.getInstance().add(photoGridAdapter.getSelectedPaths(), 1);
            selectAllItem.setIcon(Utils.getR("drawable.ic_select_all"));
          }
        selectAllItem.setChecked(!selectAllItem.isChecked());
        mListener.onItemSelected();
      }
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
