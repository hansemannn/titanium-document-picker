package droidninja.filepicker;


public class FilePickerConst
{
  public static final int REQUEST_CODE_PHOTO = 233;
  
  public static final int REQUEST_CODE_DOC = 234;
  
  public static final int REQUEST_CODE_MEDIA_DETAIL = 235;
  
  public static final int REQUEST_CODE_PERMISSION = 988;
  
  public static final int DEFAULT_MAX_COUNT = -1;
  
  public static final int DEFAULT_COLUMN_NUMBER = 3;
  
  public static final int MEDIA_PICKER = 17;
  
  public static final int DOC_PICKER = 18;
  
  public static final String KEY_SELECTED_MEDIA = "SELECTED_PHOTOS";
  
  public static final String KEY_SELECTED_DOCS = "SELECTED_DOCS";
  
  public static final String EXTRA_PICKER_TYPE = "EXTRA_PICKER_TYPE";
  
  public static final String EXTRA_SHOW_GIF = "SHOW_GIF";
  
  public static final String EXTRA_FILE_TYPE = "EXTRA_FILE_TYPE";
  
  public static final String EXTRA_BUCKET_ID = "EXTRA_BUCKET_ID";
  public static final String ALL_PHOTOS_BUCKET_ID = "ALL_PHOTOS_BUCKET_ID";
  public static final String PPT_MIME_TYPE = "application/mspowerpoint";
  public static final int FILE_TYPE_MEDIA = 1;
  public static final int FILE_TYPE_DOCUMENT = 2;
  public static final int MEDIA_TYPE_IMAGE = 1;
  public static final int MEDIA_TYPE_VIDEO = 3;
  public static final String PERMISSIONS_FILE_PICKER = "android.permission.WRITE_EXTERNAL_STORAGE";
  public static final String[] docExtensions = { "ppt", "pptx", "xls", "xlsx", "doc", "docx", "dot", "dotx" };
  public static final String PDF = "PDF";
  
  public static enum FILE_TYPE { PDF,  WORD,  EXCEL,  PPT,  TXT,  UNKNOWN;
    
    private FILE_TYPE() {}
  }
  
  public static final String PPT = "PPT";
  public static final String DOC = "DOC";
  public static final String XLS = "XLS";
  public static final String TXT = "TXT";
  public FilePickerConst() {}
}
