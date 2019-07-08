package droidninja.filepicker.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridSpacingItemDecoration extends android.support.v7.widget.RecyclerView.ItemDecoration
{
  private int spanCount;
  private int spacing;
  private boolean includeEdge;
  
  public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge)
  {
    this.spanCount = spanCount;
    this.spacing = spacing;
    this.includeEdge = includeEdge;
  }
  
  public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, android.support.v7.widget.RecyclerView.State state)
  {
    int position = parent.getChildAdapterPosition(view);
    int column = position % spanCount;
    
    if (includeEdge) {
    	outRect.left = (spacing - column * spacing / spanCount);
    	outRect.right = ((column + 1) * spacing / spanCount);
      
      if (position < spanCount) {
    	  outRect.top = spacing;
      }
      outRect.bottom = spacing;
    } else {
    	outRect.left = (column * spacing / spanCount);
    	outRect.right = (spacing - (column + 1) * spacing / spanCount);
      if (position >= spanCount) {
    	  outRect.top = spacing;
      }
    }
  }
}
