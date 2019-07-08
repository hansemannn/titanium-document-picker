package droidninja.filepicker.cursors;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.MediaStore.Files;
import android.text.TextUtils;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.cursors.loadercallbacks.FileMapResultCallback;
import droidninja.filepicker.models.Document;
import droidninja.filepicker.models.FileType;
import droidninja.filepicker.utils.FilePickerUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;




public class DocScannerTask extends AsyncTask<Void, Void, Map<FileType, List<Document>>>
{
  final String[] DOC_PROJECTION = { "_id", "_data", "mime_type", "_size", "date_added", "title" };
  
  private final FileMapResultCallback resultCallback;
  
  private final Comparator<Document> comparator;
  
  private final List<FileType> fileTypes;
  
  private final ContentResolver contentResolver;
  

  public DocScannerTask(Context context, List<FileType> fileTypes, Comparator<Document> comparator, FileMapResultCallback fileResultCallback)
  {
    contentResolver = context.getContentResolver();
    this.fileTypes = fileTypes;
    this.comparator = comparator;
    resultCallback = fileResultCallback;
  }
  
  private HashMap<FileType, List<Document>> createDocumentType(ArrayList<Document> documents) {
    HashMap<FileType, List<Document>> documentMap = new HashMap();
    
    for (final FileType fileType : fileTypes) {    	
      Predicate<Document> docContainsTypeExtension = new Predicate<Document>() {
		@Override
		public boolean test(Document document) {
			// TODO Auto-generated method stub
			return document.isThisType(fileType.extensions);
		}
      };
      
      ArrayList<Document> documentListFilteredByType = (ArrayList)FilePickerUtils.filter(documents, docContainsTypeExtension);
      
      if (comparator != null) { Collections.sort(documentListFilteredByType, comparator);
      }
      documentMap.put(fileType, documentListFilteredByType);
    }
    
    return documentMap;
  }
  
  protected Map<FileType, List<Document>> doInBackground(Void... voids) {
    ArrayList<Document> documents = new ArrayList();
    String selection = "media_type!=1 AND media_type!=3";
    Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), DOC_PROJECTION, selection, null, "date_added DESC");
    

    if (cursor != null) {
      documents = getDocumentFromCursor(cursor);
      cursor.close();
    }
    
    return createDocumentType(documents);
  }
  
  protected void onPostExecute(Map<FileType, List<Document>> documents) {
    if (resultCallback != null) {
      resultCallback.onResultCallback(documents);
    }
  }
  
  private ArrayList<Document> getDocumentFromCursor(Cursor data) {
    ArrayList<Document> documents = new ArrayList();
    while (data.moveToNext())
    {
      int imageId = data.getInt(data.getColumnIndexOrThrow("_id"));
      String path = data.getString(data.getColumnIndexOrThrow("_data"));
      String title = data.getString(data.getColumnIndexOrThrow("title"));
      
      if (path != null)
      {
        FileType fileType = getFileType(PickerManager.getInstance().getFileTypes(), path);
        File file = new File(path);
        if ((fileType != null) && (!file.isDirectory()) && (file.exists()))
        {
          Document document = new Document(imageId, title, path);
          document.setFileType(fileType);
          

          String mimeType = data.getString(data.getColumnIndexOrThrow("mime_type"));
          if ((mimeType != null) && (!TextUtils.isEmpty(mimeType))) {
            document.setMimeType(mimeType);
          } else {
            document.setMimeType("");
          }
          
          document.setSize(data
            .getString(data.getColumnIndexOrThrow("_size")));
          
          if (!documents.contains(document)) { documents.add(document);
          }
        }
      }
    }
    return documents;
  }
  
  private FileType getFileType(ArrayList<FileType> types, String path) {
    for (int index = 0; index < types.size(); index++) {
      for (String string : types.get(index).extensions) {
        if (path.endsWith(string)) return (FileType)types.get(index);
      }
    }
    return null;
  }
}
