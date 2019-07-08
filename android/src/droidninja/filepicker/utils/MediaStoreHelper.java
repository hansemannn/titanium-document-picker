package droidninja.filepicker.utils;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import droidninja.filepicker.cursors.DocScannerTask;
import droidninja.filepicker.cursors.loadercallbacks.FileResultCallback;
import droidninja.filepicker.cursors.loadercallbacks.PhotoDirLoaderCallbacks;
import droidninja.filepicker.models.Document;
import droidninja.filepicker.models.FileType;
import droidninja.filepicker.models.PhotoDirectory;
import java.util.Comparator;
import java.util.List;

public class MediaStoreHelper
{
  public MediaStoreHelper() {}
  
  public static void getPhotoDirs(FragmentActivity activity, Bundle args, FileResultCallback<PhotoDirectory> resultCallback)
  {
    if (activity.getSupportLoaderManager().getLoader(1) != null) {
      activity.getSupportLoaderManager().restartLoader(1, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
    } else
      activity.getSupportLoaderManager().initLoader(1, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
  }
  
  public static void getVideoDirs(FragmentActivity activity, Bundle args, FileResultCallback<PhotoDirectory> resultCallback) {
    if (activity.getSupportLoaderManager().getLoader(3) != null) {
      activity.getSupportLoaderManager().restartLoader(3, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
    } else {
      activity.getSupportLoaderManager().initLoader(3, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
    }
  }
  


  public static void getDocs(FragmentActivity activity, List<FileType> fileTypes, Comparator<Document> comparator, droidninja.filepicker.cursors.loadercallbacks.FileMapResultCallback fileResultCallback)
  {
    new DocScannerTask(activity, fileTypes, comparator, fileResultCallback).execute(new Void[0]);
  }
}
