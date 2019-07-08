package droidninja.filepicker.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


public class SquareRelativeLayout
  extends RelativeLayout
{
  public SquareRelativeLayout(Context context)
  {
    super(context);
  }
  
  public SquareRelativeLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }
  
  public SquareRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }
  
  @TargetApi(21)
  public SquareRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }
  

  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
  }
}
