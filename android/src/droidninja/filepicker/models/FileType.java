package droidninja.filepicker.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.DrawableRes;
import ti.filepicker.Utils;


public class FileType
  implements Parcelable
{
  public String title;
  @DrawableRes
  public int drawable;
  public String[] extensions;

  public FileType(String title, String[] extensions, int drawable)
  {
    this.title = title;
    this.extensions = extensions;
    this.drawable = drawable;
  }

  protected FileType(Parcel in) {
    title = in.readString();
    drawable = in.readInt();
    extensions = in.createStringArray();
  }

  public static final Parcelable.Creator<FileType> CREATOR = new Parcelable.Creator()
  {
    public FileType createFromParcel(Parcel in) {
      return new FileType(in);
    }

    public FileType[] newArray(int size)
    {
      return new FileType[size];
    }
  };

  public int getDrawable()
  {
    if (drawable == 0)
      return Utils.getR("drawable.icon_file_unknown");
    return drawable;
  }

  public int describeContents()
  {
    return 0;
  }

  public void writeToParcel(Parcel parcel, int i)
  {
    parcel.writeString(title);
    parcel.writeInt(drawable);
    parcel.writeStringArray(extensions);
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if ((o == null) || (getClass() != o.getClass())) { return false;
    }
    FileType fileType = (FileType)o;

    return title == null ? true : title != null ? title.equals(title) : false;
  }

  public int hashCode() {
    return title != null ? title.hashCode() : 0;
  }
}
