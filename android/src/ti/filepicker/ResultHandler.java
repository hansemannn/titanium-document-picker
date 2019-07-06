package ti.filepicker;

import java.util.ArrayList;

import org.appcelerator.kroll.KrollDict;

import android.app.Activity;
import android.content.Intent;
import droidninja.filepicker.FilePickerConst;


public class ResultHandler {
	public ResultHandler() {}
	
	public KrollDict onError(int requestCode, Exception exc) {
		KrollDict result = new KrollDict();
		result.put(Constants.Params.RESULT_PROPERTY_ERROR, true);
		result.put(Constants.Params.RESULT_PROPERTY_MESSAGE, "Error: " + exc.getLocalizedMessage());
		return result;
	}

	public KrollDict onResult(int requestCode, int resultCode, Intent data) {
		KrollDict result = new KrollDict();
		ArrayList<String> files = new ArrayList<String>();
		
		if (resultCode == Activity.RESULT_CANCELED) {
			result.put(Constants.Params.RESULT_PROPERTY_CANCEL, true);
			result.put(Constants.Params.RESULT_PROPERTY_MESSAGE, "Result cancelled");
			
		} else if (data == null) {
			result.put(Constants.Params.RESULT_PROPERTY_ERROR, true);
			result.put(Constants.Params.RESULT_PROPERTY_MESSAGE, "Result data not available");
			
		} else if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case Constants.REQUEST_CODE_DOCS:
					files.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
					result.put(Constants.Params.RESULT_PROPERTY_SUCCESS, true);
					result.put(Constants.Params.RESULT_PROPERTY_MESSAGE, "");
					break;
					
				case Constants.REQUEST_CODE_MEDIA:
					files.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
					result.put(Constants.Params.RESULT_PROPERTY_SUCCESS, true);
					result.put(Constants.Params.RESULT_PROPERTY_MESSAGE, "");
					break;
					
				default:
					result.put(Constants.Params.RESULT_PROPERTY_ERROR, true);
					result.put(Constants.Params.RESULT_PROPERTY_MESSAGE, "Result request-code not available");
					break;
			}
		}
		
		// add files array to the result dictionary
		result.put(Constants.Params.RESULT_PROPERTY_FILES, files.toArray());
		
		return result;
	}
}
