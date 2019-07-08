package droidninja.filepicker;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import droidninja.filepicker.utils.Orientation;
import ti.filepicker.Utils;




public abstract class BaseFilePickerActivity
  extends AppCompatActivity
{
  public BaseFilePickerActivity() {}

  protected void onCreate(@Nullable Bundle savedInstanceState, @LayoutRes int layout)
  {
    super.onCreate(savedInstanceState);
    setTheme(PickerManager.getInstance().getTheme());
    setContentView(layout);

    Toolbar toolbar = (Toolbar)findViewById(Utils.getR("id.toolbar"));
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    Orientation orientation = PickerManager.getInstance().getOrientation();
    if (orientation == Orientation.PORTRAIT_ONLY) {
      setRequestedOrientation(1);
    } else if (orientation == Orientation.LANDSCAPE_ONLY) {
      setRequestedOrientation(0);
    }

    initView();
  }

  protected abstract void initView();
}
