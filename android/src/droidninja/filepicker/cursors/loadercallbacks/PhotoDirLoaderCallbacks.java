package droidninja.filepicker.cursors.loadercallbacks;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.cursors.PhotoDirectoryLoader;
import droidninja.filepicker.models.PhotoDirectory;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;







public class PhotoDirLoaderCallbacks implements LoaderCallbacks<Cursor>
{
  public static final int INDEX_ALL_PHOTOS = 0;
  private WeakReference<Context> context;
  private FileResultCallback<PhotoDirectory> resultCallback;
  
  public PhotoDirLoaderCallbacks(Context context, FileResultCallback<PhotoDirectory> resultCallback)
  {
    this.context = new WeakReference(context);
    this.resultCallback = resultCallback;
  }
  
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new PhotoDirectoryLoader((Context)context.get(), args);
  }
  
  public void onLoadFinished(Loader<Cursor> loader, Cursor data)
  {
    if (data == null) return;
    List<PhotoDirectory> directories = new ArrayList();
    
    while (data.moveToNext())
    {
      int imageId = data.getInt(data.getColumnIndexOrThrow("_id"));
      String bucketId = data.getString(data.getColumnIndexOrThrow("bucket_id"));
      String name = data.getString(data.getColumnIndexOrThrow("bucket_display_name"));
      String path = data.getString(data.getColumnIndexOrThrow("_data"));
      String fileName = data.getString(data.getColumnIndexOrThrow("title"));
      int mediaType = data.getInt(data.getColumnIndexOrThrow("media_type"));
      
      PhotoDirectory photoDirectory = new PhotoDirectory();
      photoDirectory.setBucketId(bucketId);
      photoDirectory.setName(name);
      
      if (!directories.contains(photoDirectory)) {
        if ((path != null) && (path.toLowerCase().endsWith("gif"))) {
          if (PickerManager.getInstance().isShowGif()) {
            photoDirectory.addPhoto(imageId, fileName, path, mediaType);
          }
        } else {
          photoDirectory.addPhoto(imageId, fileName, path, mediaType);
        }
        
        photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow("date_added")));
        directories.add(photoDirectory);
      }
      else {
        ((PhotoDirectory)directories.get(directories.indexOf(photoDirectory))).addPhoto(imageId, fileName, path, mediaType);
      }
    }
    
    if (resultCallback != null) {
      resultCallback.onResultCallback(directories);
    }
  }
  
  public void onLoaderReset(Loader<Cursor> loader) {}
}
