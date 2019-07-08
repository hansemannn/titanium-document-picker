package droidninja.filepicker.adapters;

public abstract interface Selectable<T>
{
  public abstract boolean isSelected(T paramT);
  
  public abstract void toggleSelection(T paramT);
  
  public abstract void clearSelection();
  
  public abstract int getSelectedItemCount();
}
