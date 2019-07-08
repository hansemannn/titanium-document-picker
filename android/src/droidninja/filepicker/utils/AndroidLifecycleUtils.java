package droidninja.filepicker.utils;

import android.app.Activity;

public class AndroidLifecycleUtils
{
  public AndroidLifecycleUtils() {}
  
  public static boolean canLoadImage(android.support.v4.app.Fragment fragment)
  {
    if (fragment == null) {
      return true;
    }
    
    android.support.v4.app.FragmentActivity activity = fragment.getActivity();
    
    return canLoadImage(activity);
  }
  
  public static boolean canLoadImage(android.content.Context context) {
    if (context == null) {
      return true;
    }
    
    if (!(context instanceof Activity)) {
      return true;
    }
    
    Activity activity = (Activity)context;
    return canLoadImage(activity);
  }
  
  public static boolean canLoadImage(Activity activity) {
    if (activity == null) {
      return true;
    }
    

    boolean destroyed = (android.os.Build.VERSION.SDK_INT >= 17) && (activity.isDestroyed());
    
    if ((destroyed) || (activity.isFinishing())) {
      return false;
    }
    
    return true;
  }
}
