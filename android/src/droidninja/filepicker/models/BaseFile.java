package droidninja.filepicker.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import droidninja.filepicker.utils.FilePickerUtils;


public class BaseFile
  implements Parcelable
{
  protected int id;
  protected String name;
  protected String path;
  
  public BaseFile() {}
  
  public BaseFile(int id, String name, String path)
  {
    this.id = id;
    this.name = name;
    this.path = path;
  }
  
  protected BaseFile(Parcel in) {
    id = in.readInt();
    name = in.readString();
    path = in.readString();
  }
  
  public static final Parcelable.Creator<BaseFile> CREATOR = new Parcelable.Creator() {
    public BaseFile createFromParcel(Parcel in) {
      return new BaseFile(in);
    }
    
    public BaseFile[] newArray(int size) {
      return new BaseFile[size];
    }
  };
  
  public boolean isImage() {
    String[] types = { "jpg", "jpeg", "png", "gif" };
    return FilePickerUtils.contains(types, path);
  }
  
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BaseFile)) { return false;
    }
    BaseFile baseFile = (BaseFile)o;
    if ((path != null) && (path != null)) {
      return (id == id) && (path.equals(path));
    }
    return id == id;
  }
  
  public String getPath() {
    return path;
  }
  
  public void setPath(String path) {
    this.path = path;
  }
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public int describeContents() {
    return 0;
  }
  
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeInt(id);
    parcel.writeString(name);
    parcel.writeString(path);
  }
}
