package ti.filepicker;


public class Constants {
	// module's all constants
	public static final String LCAT = "TiFilepickerModule";
	public static final int FILE_TYPE_DOC = 1;		// default file-type
	public static final int FILE_TYPE_MEDIA = 2;
	public static final int REQUEST_CODE_DOCS = 111;
	public static final int REQUEST_CODE_MEDIA = 112;
	
	
	protected static class Params {
		// result parameters
		static final String RESULT_PROPERTY_MESSAGE = "message";
		static final String RESULT_PROPERTY_SUCCESS = "success";
		static final String RESULT_PROPERTY_CANCEL = "cancel";
		static final String RESULT_PROPERTY_ERROR = "error";
		static final String RESULT_PROPERTY_FILES = "files";
		static final String CALLBACK = "callback";
		
		// input parameters
		static final String FILE_TYPE = "fileType";
		static final String MAX_COUNT = "maxCount";
		static final String THEME = "theme";
		static final String TITLE = "title";
		static final String SELECTED_FILES = "selectedFiles";
		static final String CAMERA_ICON = "cameraIcon";
		static final String ORIENTATION = "orientation";
		static final String ENABLE_VIDEO_PICKER = "enableVideoPicker";
		static final String ENABLE_IMAGE_PICKER = "enableImagePicker";
		static final String SELECT_ALL = "enableSelectAll";
		static final String ENABLE_GIFS = "enableGif";
		static final String ENABLE_FOLDER_VIEW = "enableFolderView";
		static final String ENABLE_DOC_SUPPORT = "enableDocSupport";
		static final String ENABLE_CAMERA_SUPPORT = "enableCameraSupport";
	}
}






