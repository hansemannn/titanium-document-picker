package droidninja.filepicker.cursors.loadercallbacks;

import java.util.List;

public abstract interface FileResultCallback<T>
{
  public abstract void onResultCallback(List<T> paramList);
}
