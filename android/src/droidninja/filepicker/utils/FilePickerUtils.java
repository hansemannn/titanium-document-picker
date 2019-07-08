package droidninja.filepicker.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;


public class FilePickerUtils
{
  public FilePickerUtils() {}

  public static <T> Collection<T> filter(Collection<T> target, Predicate<T> predicate)
  {
    Collection<T> result = new ArrayList<T>();
    for (T element : target) {
      if (predicate.test(element)) {
        result.add(element);
      }
    }
    return result;
  }

  public static String getFileExtension(File file) {
    String name = file.getName();
    try {
      return name.substring(name.lastIndexOf(".") + 1);
    } catch (Exception e) {}
    return "";
  }

  public static boolean contains(String[] types, String path)
  {
    for (String string : types) {
      if (path.toLowerCase().endsWith(string)) return true;
    }
    return false;
  }

  public static <T> boolean contains2(T[] array, T v) {
    if (v == null) {
      for (T e : array)
        if (e == null)
          return true;
    } else {
      for (T e : array) {
        if ((e == v) || (v.equals(e)))
          return true;
      }
    }
    return false;
  }

  public static void notifyMediaStore(Context context, String path) {
    if ((path != null) && (!TextUtils.isEmpty(path))) {
      Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
      File f = new File(path);
      Uri contentUri = Uri.fromFile(f);
      mediaScanIntent.setData(contentUri);
      context.sendBroadcast(mediaScanIntent);
    }
  }
}
