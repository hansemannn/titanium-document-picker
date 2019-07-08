package droidninja.filepicker.cursors;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Files;
import android.support.v4.content.CursorLoader;





public class PhotoDirectoryLoader
  extends CursorLoader
{
  final String[] IMAGE_PROJECTION = { "_id", "_data", "bucket_id", "bucket_display_name", "date_added", "title" };
  






  public PhotoDirectoryLoader(Context context, Bundle args)
  {
    super(context);
    String bucketId = args.getString("EXTRA_BUCKET_ID", null);
    int mediaType = args.getInt("EXTRA_FILE_TYPE", 1);
    
    setProjection(null);
    setUri(MediaStore.Files.getContentUri("external"));
    setSortOrder("_id DESC");
    
    String selection = "media_type=1";
    

    if (mediaType == 3)
    {
      selection = "media_type=3";
    }
    

    if (bucketId != null) {
      selection = selection + " AND bucket_id='" + bucketId + "'";
    }
    setSelection(selection);
  }
  


  private PhotoDirectoryLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
  {
    super(context, uri, projection, selection, selectionArgs, sortOrder);
  }
}
