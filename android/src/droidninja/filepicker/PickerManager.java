package droidninja.filepicker;

import droidninja.filepicker.models.BaseFile;
import droidninja.filepicker.models.FileType;
import droidninja.filepicker.models.sort.SortingTypes;
import droidninja.filepicker.utils.Orientation;
import ti.filepicker.Utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;





public class PickerManager
{
  private static PickerManager ourInstance = new PickerManager();
  private int maxCount = -1;
  private boolean showImages = true;
  private int cameraDrawable;
  private SortingTypes sortingType = SortingTypes.none;
  private ArrayList<String> mediaFiles;

  public static PickerManager getInstance() { return ourInstance; }

  private ArrayList<String> docFiles;

  private LinkedHashSet<FileType> fileTypes;

  private int theme;

  private String title = null;

  private boolean showVideos;

  private boolean showGif;

  private boolean showSelectAll = false;

  private boolean docSupport = true;

  private boolean enableCamera = true;

  private Orientation orientation = Orientation.UNSPECIFIED;

  private boolean showFolderView = true;
  private String providerAuthorities;

  private PickerManager()
  {
	  cameraDrawable = Utils.getR("drawable.ic_camera");
	  theme = Utils.getR("style.LibAppTheme");
    mediaFiles = new ArrayList();
    docFiles = new ArrayList();
    fileTypes = new LinkedHashSet();
  }

  public void setMaxCount(int count) {
    reset();
    maxCount = count;
  }

  public int getMaxCount() {
    return maxCount;
  }

  public int getCurrentCount() {
    return mediaFiles.size() + docFiles.size();
  }

  public void add(String path, int type) {
    if ((path != null) && (shouldAdd())) {
      if ((!mediaFiles.contains(path)) && (type == 1)) {
        mediaFiles.add(path);
      } else if ((!docFiles.contains(path)) && (type == 2)) {
        docFiles.add(path);
      } else {}
    }
  }


  public void add(ArrayList<String> paths, int type)
  {
    for (int index = 0; index < paths.size(); index++) {
      add((String)paths.get(index), type);
    }
  }

  public void remove(String path, int type) {
    if ((type == 1) && (mediaFiles.contains(path))) {
      mediaFiles.remove(path);
    } else if (type == 2) {
      docFiles.remove(path);
    }
  }

  public boolean shouldAdd() {
    if (maxCount == -1) return true;
    return getCurrentCount() < maxCount;
  }

  public ArrayList<String> getSelectedPhotos() {
    return mediaFiles;
  }

  public ArrayList<String> getSelectedFiles() {
    return docFiles;
  }

  public ArrayList<String> getSelectedFilePaths(ArrayList<BaseFile> files) {
    ArrayList<String> paths = new ArrayList();
    for (int index = 0; index < files.size(); index++) {
      paths.add(((BaseFile)files.get(index)).getPath());
    }
    return paths;
  }

  public void reset() {
    docFiles.clear();
    mediaFiles.clear();
    fileTypes.clear();
    maxCount = -1;
  }

  public void clearSelections() {
    mediaFiles.clear();
    docFiles.clear();
  }

  public void deleteMedia(ArrayList<String> paths) {
    mediaFiles.removeAll(paths);
  }

  public int getTheme() {
    return theme;
  }

  public void setTheme(int theme) {
    this.theme = theme;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean showVideo() {
    return showVideos;
  }

  public void setShowVideos(boolean showVideos) {
    this.showVideos = showVideos;
  }

  public boolean showImages() {
    return showImages;
  }

  public void setShowImages(boolean showImages) {
    this.showImages = showImages;
  }

  public boolean isShowGif() {
    return showGif;
  }

  public void setShowGif(boolean showGif) {
    this.showGif = showGif;
  }

  public boolean isShowFolderView() {
    return showFolderView;
  }

  public void setShowFolderView(boolean showFolderView) {
    this.showFolderView = showFolderView;
  }

  public void addFileType(FileType fileType) {
    fileTypes.add(fileType);
  }

  public void addDocTypes() {
	  String[] pdfs = { "pdf" };
	    fileTypes.add(new FileType("PDF", pdfs, Utils.getR("drawable.icon_file_pdf")));

	    String[] docs = { "doc", "docx", "dot", "dotx" };
	    fileTypes.add(new FileType("DOC", docs, Utils.getR("drawable.icon_file_doc")));

	    String[] ppts = { "ppt", "pptx" };
	    fileTypes.add(new FileType("PPT", ppts, Utils.getR("drawable.icon_file_ppt")));

	    String[] xlss = { "xls", "xlsx" };
	    fileTypes.add(new FileType("XLS", xlss, Utils.getR("drawable.icon_file_xls")));

	    String[] txts = { "txt" };
	    fileTypes.add(new FileType("TXT", txts, Utils.getR("drawable.icon_file_unknown")));
  }

  public ArrayList<FileType> getFileTypes() {
    return new ArrayList(fileTypes);
  }

  public boolean isDocSupport() {
    return docSupport;
  }

  public void setDocSupport(boolean docSupport) {
    this.docSupport = docSupport;
  }

  public boolean isEnableCamera() {
    return enableCamera;
  }

  public void setEnableCamera(boolean enableCamera) {
    this.enableCamera = enableCamera;
  }

  public Orientation getOrientation() {
    return orientation;
  }

  public void setOrientation(Orientation orientation) {
    this.orientation = orientation;
  }

  public String getProviderAuthorities() {
    return providerAuthorities;
  }

  public void setProviderAuthorities(String providerAuthorities) {
    this.providerAuthorities = providerAuthorities;
  }

  public void setCameraDrawable(int drawable) {
    cameraDrawable = drawable;
  }

  public int getCameraDrawable() {
    return cameraDrawable;
  }

  public boolean hasSelectAll() {
    return (maxCount == -1) && (showSelectAll);
  }

  public void enableSelectAll(boolean showSelectAll) {
    this.showSelectAll = showSelectAll;
  }

  public SortingTypes getSortingType() {
    return sortingType;
  }

  public void setSortingType(SortingTypes sortingType) {
    this.sortingType = sortingType;
  }
}
