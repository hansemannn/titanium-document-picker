package droidninja.filepicker.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import droidninja.filepicker.PickerManager;
import java.io.File;
import java.io.IOException;


public class ImageCaptureManager
{
  private static final String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
  public static final int REQUEST_TAKE_PHOTO = 257;
  private String mCurrentPhotoPath;
  private Context mContext;

  public ImageCaptureManager(Context mContext)
  {
    this.mContext = mContext;
  }

  private File createImageFile()
    throws IOException
  {
    String imageFileName = "JPEG_" + System.currentTimeMillis() + ".jpg";
    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    if ((!storageDir.exists()) &&
      (!storageDir.mkdir())) {
      Log.e("TAG", "Throwing Errors....");
      throw new IOException();
    }


    File image = new File(storageDir, imageFileName);

    mCurrentPhotoPath = image.getAbsolutePath();
    return image;
  }

  public Intent dispatchTakePictureIntent(Context context) throws IOException
  {
    Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");

    if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null)
    {
      if (Build.VERSION.SDK_INT >= 24) {
        File newFile = createImageFile();
        takePictureIntent.addFlags(1);
        takePictureIntent.addFlags(2);
        Uri photoURI = FileProvider.getUriForFile(context, PickerManager.getInstance().getProviderAuthorities(), newFile);
        takePictureIntent.putExtra("output", photoURI);
      } else {
        takePictureIntent.putExtra("output", Uri.fromFile(createImageFile()));
      }
      return takePictureIntent;
    }
    return null;
  }

  public String notifyMediaStoreDatabase()
  {
    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");

    if (TextUtils.isEmpty(mCurrentPhotoPath)) {
      return null;
    }

    File f = new File(mCurrentPhotoPath);
    Uri contentUri = Uri.fromFile(f);
    mediaScanIntent.setData(contentUri);
    mContext.sendBroadcast(mediaScanIntent);

    return mCurrentPhotoPath;
  }

  public String getCurrentPhotoPath()
  {
    return mCurrentPhotoPath;
  }

  public void onSaveInstanceState(Bundle savedInstanceState)
  {
    if ((savedInstanceState != null) && (mCurrentPhotoPath != null)) {
      savedInstanceState.putString("mCurrentPhotoPath", mCurrentPhotoPath);
    }
  }

  public void onRestoreInstanceState(Bundle savedInstanceState) {
    if ((savedInstanceState != null) && (savedInstanceState.containsKey("mCurrentPhotoPath"))) {
      mCurrentPhotoPath = savedInstanceState.getString("mCurrentPhotoPath");
    }
  }
}
