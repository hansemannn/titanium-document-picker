package droidninja.filepicker.models.sort;

import droidninja.filepicker.models.Document;
import java.util.Comparator;





public class NameComparator
  implements Comparator<Document>
{
  protected NameComparator() {}
  
  public int compare(Document o1, Document o2)
  {
    return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
  }
}
