package droidninja.filepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import droidninja.filepicker.fragments.DocFragment.DocFragmentListener;
import droidninja.filepicker.fragments.DocPickerFragment;
import droidninja.filepicker.fragments.DocPickerFragment.DocPickerFragmentListener;
import droidninja.filepicker.fragments.MediaPickerFragment;
import droidninja.filepicker.fragments.MediaPickerFragment.MediaPickerFragmentListener;
import droidninja.filepicker.fragments.PhotoPickerFragmentListener;
import droidninja.filepicker.utils.FragmentUtil;
import ti.filepicker.Utils;

import java.util.ArrayList;

public class FilePickerActivity extends BaseFilePickerActivity implements PhotoPickerFragmentListener, DocFragmentListener, DocPickerFragmentListener, MediaPickerFragmentListener
{
  private static final String TAG = FilePickerActivity.class.getSimpleName();

  public FilePickerActivity() {}

  protected void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState, Utils.getR("layout.activity_file_picker")); }

  private int type;
  protected void initView() {
    Intent intent = getIntent();
    if (intent != null)
    {
      ArrayList<String> selectedPaths = intent.getStringArrayListExtra("SELECTED_PHOTOS");
      type = intent.getIntExtra("EXTRA_PICKER_TYPE", 17);

      if (selectedPaths != null)
      {
        if (PickerManager.getInstance().getMaxCount() == 1) {
          selectedPaths.clear();
        }

        PickerManager.getInstance().clearSelections();
        if (type == 17) {
          PickerManager.getInstance().add(selectedPaths, 1);
        } else {
          PickerManager.getInstance().add(selectedPaths, 2);
        }
      } else {
        selectedPaths = new ArrayList();
      }

      setToolbarTitle(PickerManager.getInstance().getCurrentCount());
      openSpecificFragment(type, selectedPaths);
    }
  }

  private void setToolbarTitle(int count) {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      int maxCount = PickerManager.getInstance().getMaxCount();
      if ((maxCount == -1) && (count > 0)) {
        actionBar.setTitle(String.format(getString(Utils.getR("string.attachments_num")), new Object[] { Integer.valueOf(count) }));
      } else if ((maxCount > 0) && (count > 0)) {
        actionBar.setTitle(
          String.format(getString(Utils.getR("string.attachments_title_text")), new Object[] { Integer.valueOf(count), Integer.valueOf(maxCount) }));
      } else if (!TextUtils.isEmpty(PickerManager.getInstance().getTitle())) {
        actionBar.setTitle(PickerManager.getInstance().getTitle());
      }
      else if (type == 17) {
        actionBar.setTitle(Utils.getR("string.select_photo_text"));
      } else {
        actionBar.setTitle(Utils.getR("string.select_doc_text"));
      }
    }
  }

  private void openSpecificFragment(int type, @Nullable ArrayList<String> selectedPaths)
  {
    if (type == 17) {
      MediaPickerFragment photoFragment = MediaPickerFragment.newInstance();
      FragmentUtil.addFragment(this, Utils.getR("id.container"), photoFragment);
    } else {
      if (PickerManager.getInstance().isDocSupport()) { PickerManager.getInstance().addDocTypes();
      }
      DocPickerFragment photoFragment = DocPickerFragment.newInstance();
      FragmentUtil.addFragment(this, Utils.getR("id.container"), photoFragment);
    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(Utils.getR("menu.picker_menu"), menu);
    MenuItem menuItem = menu.findItem(Utils.getR("id.action_done"));
    if (menuItem != null) {
      if (PickerManager.getInstance().getMaxCount() == 1) {
        menuItem.setVisible(false);
      } else {
        menuItem.setVisible(true);
      }
    }
    return super.onCreateOptionsMenu(menu);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    int i = item.getItemId();
    if (i == Utils.getR("id.action_done")) {
      if (type == 17) {
        returnData(PickerManager.getInstance().getSelectedPhotos());
      } else {
        returnData(PickerManager.getInstance().getSelectedFiles());
      }

      return true; }
    if (i == 16908332) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void onBackPressed() {
    super.onBackPressed();
    PickerManager.getInstance().reset();
    setResult(0);
    finish();
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
    case 235:
      if (resultCode == -1) {
        if (type == 17) {
          returnData(PickerManager.getInstance().getSelectedPhotos());
        } else {
          returnData(PickerManager.getInstance().getSelectedFiles());
        }
      } else {
        setToolbarTitle(PickerManager.getInstance().getCurrentCount());
      }
      break;
    }
  }

  private void returnData(ArrayList<String> paths) {
    Intent intent = new Intent();
    if (type == 17) {
      intent.putStringArrayListExtra("SELECTED_PHOTOS", paths);
    } else {
      intent.putStringArrayListExtra("SELECTED_DOCS", paths);
    }

    setResult(-1, intent);
    finish();
  }

  public void onItemSelected() {
    int currentCount = PickerManager.getInstance().getCurrentCount();
    setToolbarTitle(currentCount);

    if ((PickerManager.getInstance().getMaxCount() == 1) && (currentCount == 1)) {
      returnData(type == 17 ?
        PickerManager.getInstance().getSelectedPhotos() :
        PickerManager.getInstance().getSelectedFiles());
    }
  }
}
