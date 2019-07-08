package droidninja.filepicker.models.sort;

import droidninja.filepicker.models.Document;
import java.util.Comparator;





public enum SortingTypes
{
  name(new NameComparator()),  none(null);
  
  private final Comparator<Document> comparator;
  
  private SortingTypes(Comparator<Document> comparator) {
    this.comparator = comparator;
  }
  
  public Comparator<Document> getComparator() {
    return comparator;
  }
}
