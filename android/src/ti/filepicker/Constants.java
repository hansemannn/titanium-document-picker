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
	}
}






