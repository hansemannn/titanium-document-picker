package droidninja.filepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.bumptech.glide.RequestManager;
import droidninja.filepicker.adapters.PhotoGridAdapter;
import droidninja.filepicker.cursors.loadercallbacks.FileResultCallback;
import droidninja.filepicker.models.Media;
import droidninja.filepicker.models.PhotoDirectory;
import droidninja.filepicker.utils.AndroidLifecycleUtils;
import droidninja.filepicker.utils.MediaStoreHelper;
import ti.filepicker.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MediaDetailsActivity extends BaseFilePickerActivity implements droidninja.filepicker.adapters.FileAdapterListener
{
  private static final int SCROLL_THRESHOLD = 30;
  private RecyclerView recyclerView;
  private TextView emptyView;
  private RequestManager mGlideRequestManager;
  private PhotoGridAdapter photoGridAdapter;
  private int fileType;
  private MenuItem selectAllItem;
  private PhotoDirectory photoDirectory;

  public MediaDetailsActivity() {}

  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState, Utils.getR("layout.activity_media_details"));
  }

  protected void initView() {
	  mGlideRequestManager = com.bumptech.glide.Glide.with(this);
    Intent intent = getIntent();
    if (intent != null)
    {

      fileType = intent.getIntExtra("EXTRA_FILE_TYPE", 1);
      photoDirectory = ((PhotoDirectory)intent.getParcelableExtra(PhotoDirectory.class.getSimpleName()));
      if (photoDirectory != null)
      {
        setUpView();
        setTitle(0);
      }
    }
  }

  public void setTitle(int count) {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      int maxCount = PickerManager.getInstance().getMaxCount();
      if ((maxCount == -1) && (count > 0)) {
        actionBar.setTitle(String.format(getString(Utils.getR("string.attachments_num")), new Object[] { Integer.valueOf(count) }));
      } else if ((maxCount > 0) && (count > 0)) {
        actionBar.setTitle(
          String.format(getString(Utils.getR("string.attachments_title_text")), new Object[] { Integer.valueOf(count), Integer.valueOf(maxCount) }));
      } else {
        actionBar.setTitle(photoDirectory.getName());
      }
    }
  }

  private void setUpView() {
    recyclerView = ((RecyclerView)findViewById(Utils.getR("id.recyclerview")));
    emptyView = ((TextView)findViewById(Utils.getR("id.empty_view")));

    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, 1);

    layoutManager.setGapStrategy(2);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (Math.abs(dy) > 30) {
          mGlideRequestManager.pauseRequests();
        } else {
          MediaDetailsActivity.this.resumeRequestsIfNotDestroyed();
        }
      }

      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == 0) {
          MediaDetailsActivity.this.resumeRequestsIfNotDestroyed();
        }
      }
    });
  }

  protected void onResume() {
    super.onResume();
    getDataFromMedia(photoDirectory.getBucketId());
  }

  private void getDataFromMedia(String bucketId) {
    Bundle mediaStoreArgs = new Bundle();
    mediaStoreArgs.putBoolean("SHOW_GIF", false);
    mediaStoreArgs.putString("EXTRA_BUCKET_ID", bucketId);

    mediaStoreArgs.putInt("EXTRA_FILE_TYPE", fileType);

    if (fileType == 1) {
      MediaStoreHelper.getPhotoDirs(this, mediaStoreArgs, new FileResultCallback<PhotoDirectory>() {
        public void onResultCallback(List<PhotoDirectory> dirs) {
          MediaDetailsActivity.this.updateList(dirs);
        }
      });
    } else if (fileType == 3) {
      MediaStoreHelper.getVideoDirs(this, mediaStoreArgs, new FileResultCallback<PhotoDirectory>() {
        public void onResultCallback(List<PhotoDirectory> dirs) {
          MediaDetailsActivity.this.updateList(dirs);
        }
      });
    }
  }

  private void updateList(List<PhotoDirectory> dirs) {
    ArrayList<Media> medias = new ArrayList<Media>();
    for (int i = 0; i < dirs.size(); i++) {
      medias.addAll(((PhotoDirectory)dirs.get(i)).getMedias());
    }

    java.util.Collections.sort(medias, new Comparator<Object>() {
    		public int compare(Object a, Object b) {
    			return ((Media) b).getId() - ((Media) a).getId();
    		}
    });

    if (medias.size() > 0) {
      emptyView.setVisibility(8);
      recyclerView.setVisibility(0);
    } else {
      emptyView.setVisibility(0);
      recyclerView.setVisibility(8);
      return;
    }

    if (photoGridAdapter != null) {
      photoGridAdapter.setData(medias);
      photoGridAdapter.notifyDataSetChanged();
    }
    else {
    	photoGridAdapter = new PhotoGridAdapter(this, mGlideRequestManager, medias, PickerManager.getInstance().getSelectedPhotos(), false, this);
      recyclerView.setAdapter(photoGridAdapter);
    }

    if (PickerManager.getInstance().getMaxCount() == -1) {
      if ((photoGridAdapter != null) && (selectAllItem != null) &&
        (photoGridAdapter.getItemCount() == photoGridAdapter.getSelectedItemCount())) {
        selectAllItem.setIcon(Utils.getR("drawable.ic_select_all"));
        selectAllItem.setChecked(true);
      }

      setTitle(PickerManager.getInstance().getCurrentCount());
    }
  }

  private void resumeRequestsIfNotDestroyed() {
    if (!AndroidLifecycleUtils.canLoadImage(this)) {
      return;
    }

    mGlideRequestManager.resumeRequests();
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    if (PickerManager.getInstance().getMaxCount() > 1) {
      getMenuInflater().inflate(Utils.getR("menu.media_detail_menu"), menu);
      selectAllItem = menu.findItem(Utils.getR("id.action_select"));
      selectAllItem.setVisible(PickerManager.getInstance().hasSelectAll());
    }
    return super.onCreateOptionsMenu(menu);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == Utils.getR("id.action_done")) {
      setResult(-1, null);
      finish();

      return true; }
    if (itemId == Utils.getR("id.action_select")) {
      if ((photoGridAdapter != null) && (selectAllItem != null)) {
        if (selectAllItem.isChecked()) {
          PickerManager.getInstance().deleteMedia(photoGridAdapter.getSelectedPaths());
          photoGridAdapter.clearSelection();

          selectAllItem.setIcon(Utils.getR("drawable.ic_deselect_all"));
        } else {
          photoGridAdapter.selectAll();
          PickerManager.getInstance()
            .add(photoGridAdapter.getSelectedPaths(), 1);
          selectAllItem.setIcon(Utils.getR("drawable.ic_select_all"));
        }
        selectAllItem.setChecked(!selectAllItem.isChecked());
        setTitle(PickerManager.getInstance().getCurrentCount());
      }
      return true; }
    if (itemId == 16908332) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void onItemSelected() {
    int maxCount = PickerManager.getInstance().getMaxCount();
    if (maxCount == 1) {
      setResult(-1, null);
      finish();
    }
    setTitle(PickerManager.getInstance().getCurrentCount());
  }

  public void onBackPressed() {
    setResult(0, null);
    finish();
  }
}
