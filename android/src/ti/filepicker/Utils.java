package ti.filepicker;


import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiRHelper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;


public class Utils {
	public static int getR(String path) {
		try {
			return TiRHelper.getResource(path);
			
		} catch (Exception exc) {
			return -1;
		}
	}
	
	public static boolean hasStoragePermissions() {
		if (Build.VERSION.SDK_INT < 23) {
			return true;
		}
		
		Context context = TiApplication.getInstance().getApplicationContext();
		
		if (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
				&& context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
			return true;
		}
		
		return false;
    }
}
