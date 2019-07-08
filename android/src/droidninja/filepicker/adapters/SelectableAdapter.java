package droidninja.filepicker.adapters;


import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import droidninja.filepicker.models.BaseFile;
import java.util.ArrayList;
import java.util.List;


public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder, T extends BaseFile>
extends RecyclerView.Adapter<VH>
{
  private static final String TAG = SelectableAdapter.class.getSimpleName();
  private List<T> items;
  protected List<T> selectedPhotos;
  
  public SelectableAdapter(List<T> items, List<String> selectedPaths)
  {
    this.items = items;
    selectedPhotos = new ArrayList();
    
    addPathsToSelections(selectedPaths);
  }
  
  private void addPathsToSelections(List<String> selectedPaths) {
    if (selectedPaths == null) { return;
    }
    for (int index = 0; index < items.size(); index++) {
      for (int jindex = 0; jindex < selectedPaths.size(); jindex++) {
        if (((BaseFile)items.get(index)).getPath().equals(selectedPaths.get(jindex))) {
          selectedPhotos.add(items.get(index));
        }
      }
    }
  }
  





  public boolean isSelected(T photo)
  {
    return selectedPhotos.contains(photo);
  }
  




  public void toggleSelection(T photo)
  {
    if (selectedPhotos.contains(photo)) {
      selectedPhotos.remove(photo);
    } else {
      selectedPhotos.add(photo);
    }
  }
  


  public void clearSelection()
  {
    selectedPhotos.clear();
    notifyDataSetChanged();
  }
  
  public void selectAll() {
    selectedPhotos.clear();
    selectedPhotos.addAll(items);
    notifyDataSetChanged();
  }
  
  public int getSelectedItemCount() {
    return selectedPhotos.size();
  }
  
  public void setData(List<T> items) {
    this.items = items;
  }
  
  public List<T> getItems() {
    return items;
  }
  
  public ArrayList<String> getSelectedPaths() {
    ArrayList<String> paths = new ArrayList();
    for (int index = 0; index < selectedPhotos.size(); index++) {
      paths.add(((BaseFile)selectedPhotos.get(index)).getPath());
    }
    return paths;
  }
}
