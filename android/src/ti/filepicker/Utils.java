package ti.filepicker;



import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiConvert;
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
	
	public static String getStringOption(KrollDict options, String key) {
		String value = "";
		 
		if (options.containsKeyAndNotNull(key)) {
			value = TiConvert.toString(options.get(key), "");
		}
		
		return value;
	}
	
	public static int getIntOption(KrollDict options, String key) {
		int value = -1;
		 
		if (options.containsKeyAndNotNull(key)) {
			value = TiConvert.toInt(options.get(key), -1);
		}
		
		return value;
	}
	
	public static boolean getBoolOption(KrollDict options, String key) {
		boolean value = true;
		 
		if (options.containsKeyAndNotNull(key)) {
			value = TiConvert.toBoolean(options.get(key), true);
		}
		
		return value;
	}
	
	public static boolean getBoolOption(KrollDict options, String key, boolean defaultValue) {
		boolean value = defaultValue;
		 
		if (options.containsKeyAndNotNull(key)) {
			value = TiConvert.toBoolean(options.get(key), defaultValue);
		}
		
		return value;
	}
	
	public static String[] getArrayOption(KrollDict options, String key) {
		if (options.containsKeyAndNotNull(key)) {
			return options.getStringArray(key);
			
		} else {
			return null;
		}
	}
}



/* 
	setActivityTitle(String title)	used to set title for toolbar
	setActivityTheme(int theme)	used to set theme for toolbar (must be an non-actionbar theme or use LibAppTheme)
	setMaxCount(int maxCount)	used to specify maximum count of media picks (dont use if you want no limit)
	
	enableVideoPicker(boolean status)	added video picker alongside images
	enableImagePicker(boolean status)	added option to disable image picker
	enableSelectAll(boolean status)	added option to enable/disable select all feature(it will only work with no limit option)
	showGifs(boolean status)	to show gifs images in the picker
	showFolderView(boolean status)	if you want to show folder type pick view, enable this. (Enabled by default)
	enableDocSupport(boolean status)	If you want to enable/disable default document picker, use this method. (Enabled by default)
	enableCameraSupport(boolean status)	to show camera in the picker (Enabled by default)

	setSelectedFiles(ArrayList selectedPhotos)	to show already selected items
	setCameraPlaceholder(int drawable)	set custom camera drawable
	withOrientation(Orientation type)	In case, if you want to set orientation, use ActivityInfo for constants (default=ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
	
	addFileSupport(String title, String[] extensions, @DrawableRes int drawable)	If you want to specify custom file type, use this method. (example below)
*/