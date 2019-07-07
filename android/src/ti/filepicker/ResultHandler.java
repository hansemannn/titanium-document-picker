package ti.filepicker;

import java.util.ArrayList;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.io.TiFileFactory;

import android.app.Activity;
import android.content.Intent;
import droidninja.filepicker.FilePickerConst;


public class ResultHandler {
	public ResultHandler() {}
	
	private ArrayList<String> prepareTitaniumFilePaths(ArrayList<String> pickerFiles, KrollDict result) {
		// add the 'file://' prefix to all file paths so Ti.Filesystem can recognise it
		ArrayList<String> titaniumFiles = new ArrayList<String>();
		for (String pickerFile : pickerFiles) {
			titaniumFiles.add(Constants.TI_FILE_PREFIX + pickerFile);
		}
		
		// add 'success' after modifying all files path 
		result.put(Constants.Params.RESULT_PROPERTY_SUCCESS, true);
		
		return titaniumFiles;
	}
	
	public KrollDict onError(int requestCode, Exception exc) {
		KrollDict result = new KrollDict();
		result.put(Constants.Params.RESULT_PROPERTY_CANCEL, false);
		result.put(Constants.Params.RESULT_PROPERTY_ERROR, true);
		result.put(Constants.Params.RESULT_PROPERTY_SUCCESS, false);
		result.put(Constants.Params.RESULT_PROPERTY_MESSAGE, "Error: " + exc.getLocalizedMessage());
		return result;
	}

	public KrollDict onResult(int requestCode, int resultCode, Intent data) {
		ArrayList<String> files = new ArrayList<String>();
		
		KrollDict result = new KrollDict();
		result.put(Constants.Params.RESULT_PROPERTY_CANCEL, false);
		result.put(Constants.Params.RESULT_PROPERTY_ERROR, false);
		result.put(Constants.Params.RESULT_PROPERTY_SUCCESS, false);
		result.put(Constants.Params.RESULT_PROPERTY_MESSAGE, "");
		
		if (resultCode == Activity.RESULT_CANCELED) {
			result.put(Constants.Params.RESULT_PROPERTY_CANCEL, true);
			result.put(Constants.Params.RESULT_PROPERTY_MESSAGE, "TiFilePicker cancelled");
			
		} else if (data == null) {
			result.put(Constants.Params.RESULT_PROPERTY_ERROR, true);
			result.put(Constants.Params.RESULT_PROPERTY_MESSAGE, "Error: Data not available");
			
		} else if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case FilePickerConst.REQUEST_CODE_DOC:
					files = prepareTitaniumFilePaths( data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS), result );
					break;
					
				case FilePickerConst.REQUEST_CODE_PHOTO:
					files = prepareTitaniumFilePaths( data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA), result );
					break;
					
				default:
					result.put(Constants.Params.RESULT_PROPERTY_ERROR, true);
					result.put(Constants.Params.RESULT_PROPERTY_MESSAGE, "Error: Unexpected error occurred");
					break;
			}
		}
		
		// add files array to the result dictionary
		result.put(Constants.Params.RESULT_PROPERTY_FILES, files.toArray());
		
		return result;
	}
	
	
}


