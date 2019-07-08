package droidninja.filepicker.cursors.loadercallbacks;

import droidninja.filepicker.models.Document;
import droidninja.filepicker.models.FileType;
import java.util.List;
import java.util.Map;

public abstract interface FileMapResultCallback
{
  public abstract void onResultCallback(Map<FileType, List<Document>> paramMap);
}
