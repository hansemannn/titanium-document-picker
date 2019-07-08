package droidninja.filepicker.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;


public class SmoothCheckBox extends View implements android.widget.Checkable
{
  private static final String KEY_INSTANCE_STATE = "InstanceState";
  private static final int COLOR_TICK = -1;
  private static final int COLOR_UNCHECKED = -1;
  private static final int COLOR_CHECKED = Color.parseColor("#FB4846");
  private static final int COLOR_FLOOR_UNCHECKED = Color.parseColor("#DFDFDF");
  private static final int DEF_DRAW_SIZE = 25;
  private static final int DEF_ANIM_DURATION = 300;
  private Paint mPaint;
  private Paint mTickPaint;
  private Paint mFloorPaint;
  private Point[] mTickPoints;
  private Point mCenterPoint;
  private Path mTickPath;
  private float mLeftLineDistance; private float mRightLineDistance; private float mDrewDistance; private float mScaleVal = 1.0F; private float mFloorScale = 1.0F;
  private int mWidth;
  private int mAnimDuration = 300;
  private int mStrokeWidth;
  private int mCheckedColor;
  private int mUnCheckedColor;

  public SmoothCheckBox(Context context) {
    this(context, null);
  }

  public SmoothCheckBox(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SmoothCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }

  @android.annotation.TargetApi(21)
  public SmoothCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(attrs);
  }

  private void init(AttributeSet attrs)
  {
    int tickColor = -1;
    mFloorColor = COLOR_FLOOR_UNCHECKED;
    mCheckedColor = COLOR_CHECKED;
    mUnCheckedColor = -1;
    mStrokeWidth = 2;

    mFloorUnCheckedColor = mFloorColor;
    mTickPaint = new Paint(1);
    mTickPaint.setStyle(Paint.Style.STROKE);
    mTickPaint.setStrokeCap(Paint.Cap.ROUND);
    mTickPaint.setColor(tickColor);

    mFloorPaint = new Paint(1);
    mFloorPaint.setStyle(Paint.Style.FILL);
    mFloorPaint.setColor(mFloorColor);

    mPaint = new Paint(1);
    mPaint.setStyle(Paint.Style.FILL);
    mPaint.setColor(mCheckedColor);

    mTickPath = new Path();
    mCenterPoint = new Point();
    mTickPoints = new Point[3];
    mTickPoints[0] = new Point();
    mTickPoints[1] = new Point();
    mTickPoints[2] = new Point();

    setOnClickListener(new android.view.View.OnClickListener()
    {
      public void onClick(View v) {
        toggle();
        mTickDrawing = false;
        mDrewDistance = 0.0f;
        if (isChecked()) {
          SmoothCheckBox.this.startCheckedAnimation();
        } else {
          SmoothCheckBox.this.startUnCheckedAnimation();
        }
      }
    });
  }

  protected Parcelable onSaveInstanceState()
  {
    Bundle bundle = new Bundle();
    bundle.putParcelable("InstanceState", super.onSaveInstanceState());
    bundle.putBoolean("InstanceState", isChecked());
    return bundle;
  }

  protected void onRestoreInstanceState(Parcelable state)
  {
    if ((state instanceof Bundle)) {
      Bundle bundle = (Bundle)state;
      boolean isChecked = bundle.getBoolean("InstanceState");
      setChecked(isChecked);
      super.onRestoreInstanceState(bundle.getParcelable("InstanceState"));
      return;
    }
    super.onRestoreInstanceState(state);
  }

  public boolean isChecked()
  {
    return mChecked;
  }

  public void toggle()
  {
    setChecked(!isChecked());
  }

  public void setChecked(boolean checked)
  {
    mChecked = checked;
    reset();
    invalidate();
    if (mListener != null) {
      mListener.onCheckedChanged(this, mChecked);
    }
  }

  private int mFloorColor;
  private int mFloorUnCheckedColor;
  private boolean mChecked;
  private boolean mTickDrawing;
  private OnCheckedChangeListener mListener;
  public void setChecked(boolean checked, boolean animate) {
    if (animate) {
      mTickDrawing = false;
      mChecked = checked;
      mDrewDistance = 0.0F;
      if (checked) {
        startCheckedAnimation();
      } else {
        startUnCheckedAnimation();
      }
      if (mListener != null) {
        mListener.onCheckedChanged(this, mChecked);
      }
    }
    else {
      setChecked(checked);
    }
  }

  private void reset() {
    mTickDrawing = true;
    mFloorScale = 1.0F;
    mScaleVal = (isChecked() ? 0.0F : 1.0F);
    mFloorColor = (isChecked() ? mCheckedColor : mFloorUnCheckedColor);
    mDrewDistance = (isChecked() ? mLeftLineDistance + mRightLineDistance : 0.0F);
  }

  private int measureSize(int measureSpec) {
    int defSize = dp2px(getContext(), 25.0F);
    int specSize = View.MeasureSpec.getSize(measureSpec);
    int specMode = View.MeasureSpec.getMode(measureSpec);

    int result = 0;
    switch (specMode) {
    case 0:
    case -2147483648:
      result = Math.min(defSize, specSize);
      break;
    case 1073741824:
      result = specSize;
    }

    return result;
  }

  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension(measureSize(widthMeasureSpec), measureSize(heightMeasureSpec));
  }

  protected void onLayout(boolean changed, int left, int top, int right, int bottom)
  {
    mWidth = getMeasuredWidth();
    mStrokeWidth = (mStrokeWidth == 0 ? getMeasuredWidth() / 10 : mStrokeWidth);
    mStrokeWidth = (mStrokeWidth > getMeasuredWidth() / 5 ? getMeasuredWidth() / 5 : mStrokeWidth);
    mStrokeWidth = (mStrokeWidth < 3 ? 3 : mStrokeWidth);
    mCenterPoint.x = (mWidth / 2);
    mCenterPoint.y = (getMeasuredHeight() / 2);

    mTickPoints[0].x = Math.round(getMeasuredWidth() / 30.0F * 7.0F);
    mTickPoints[0].y = Math.round(getMeasuredHeight() / 30.0F * 14.0F);
    mTickPoints[1].x = Math.round(getMeasuredWidth() / 30.0F * 13.0F);
    mTickPoints[1].y = Math.round(getMeasuredHeight() / 30.0F * 20.0F);
    mTickPoints[2].x = Math.round(getMeasuredWidth() / 30.0F * 22.0F);
    mTickPoints[2].y = Math.round(getMeasuredHeight() / 30.0F * 10.0F);

    mLeftLineDistance = ((float)Math.sqrt(Math.pow(mTickPoints[1].x - mTickPoints[0].x, 2.0D) +
      Math.pow(mTickPoints[1].y - mTickPoints[0].y, 2.0D)));
    mRightLineDistance = ((float)Math.sqrt(Math.pow(mTickPoints[2].x - mTickPoints[1].x, 2.0D) +
      Math.pow(mTickPoints[2].y - mTickPoints[1].y, 2.0D)));
    mTickPaint.setStrokeWidth(mStrokeWidth);
  }

  protected void onDraw(Canvas canvas)
  {
    drawBorder(canvas);
    drawCenter(canvas);
    drawTick(canvas);
  }

  private void drawCenter(Canvas canvas) {
    mPaint.setColor(mUnCheckedColor);
    float radius = (mCenterPoint.x - mStrokeWidth) * mScaleVal;
    canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, radius, mPaint);
  }

  private void drawBorder(Canvas canvas) {
    mFloorPaint.setColor(mFloorColor);
    int radius = mCenterPoint.x;
    canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, radius * mFloorScale, mFloorPaint);
  }

  private void drawTick(Canvas canvas) {
    if ((mTickDrawing) && (isChecked())) {
      drawTickPath(canvas);
    }
  }

  private void drawTickPath(Canvas canvas) {
    mTickPath.reset();

    if (mDrewDistance < mLeftLineDistance) {
      float step = mWidth / 20.0F < 3.0F ? 3.0F : mWidth / 20.0F;
      mDrewDistance += step;
      float stopX = mTickPoints[0].x + (mTickPoints[1].x - mTickPoints[0].x) * mDrewDistance / mLeftLineDistance;
      float stopY = mTickPoints[0].y + (mTickPoints[1].y - mTickPoints[0].y) * mDrewDistance / mLeftLineDistance;

      mTickPath.moveTo(mTickPoints[0].x, mTickPoints[0].y);
      mTickPath.lineTo(stopX, stopY);
      canvas.drawPath(mTickPath, mTickPaint);

      if (mDrewDistance > mLeftLineDistance) {
        mDrewDistance = mLeftLineDistance;
      }
    }
    else {
      mTickPath.moveTo(mTickPoints[0].x, mTickPoints[0].y);
      mTickPath.lineTo(mTickPoints[1].x, mTickPoints[1].y);
      canvas.drawPath(mTickPath, mTickPaint);


      if (mDrewDistance < mLeftLineDistance + mRightLineDistance) {
        float stopX = mTickPoints[1].x + (mTickPoints[2].x - mTickPoints[1].x) * (mDrewDistance - mLeftLineDistance) / mRightLineDistance;
        float stopY = mTickPoints[1].y - (mTickPoints[1].y - mTickPoints[2].y) * (mDrewDistance - mLeftLineDistance) / mRightLineDistance;

        mTickPath.reset();
        mTickPath.moveTo(mTickPoints[1].x, mTickPoints[1].y);
        mTickPath.lineTo(stopX, stopY);
        canvas.drawPath(mTickPath, mTickPaint);

        float step = mWidth / 20 < 3 ? 3.0F : mWidth / 20;
        mDrewDistance += step;
      } else {
        mTickPath.reset();
        mTickPath.moveTo(mTickPoints[1].x, mTickPoints[1].y);
        mTickPath.lineTo(mTickPoints[2].x, mTickPoints[2].y);
        canvas.drawPath(mTickPath, mTickPaint);
      }
    }


    if (mDrewDistance < mLeftLineDistance + mRightLineDistance) {
      postDelayed(new Runnable()
      {

        public void run() { postInvalidate(); } }, 10L);
    }
  }


  private void startCheckedAnimation()
  {
    ValueAnimator animator = ValueAnimator.ofFloat(new float[] { 1.0F, 0.0F });
    animator.setDuration(mAnimDuration / 3 * 2);
    animator.setInterpolator(new LinearInterpolator());
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator animation) {
        mScaleVal = ((Float)animation.getAnimatedValue()).floatValue();
        mFloorColor = SmoothCheckBox.getGradientColor(mUnCheckedColor, mCheckedColor, 1.0F - mScaleVal);
        postInvalidate();
      }
    });
    animator.start();

    ValueAnimator floorAnimator = ValueAnimator.ofFloat(new float[] { 1.0F, 0.8F, 1.0F });
    floorAnimator.setDuration(mAnimDuration);
    floorAnimator.setInterpolator(new LinearInterpolator());
    floorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator animation) {
        mFloorScale = ((Float)animation.getAnimatedValue()).floatValue();
        postInvalidate();
      }
    });
    floorAnimator.start();

    drawTickDelayed();
  }

  private void startUnCheckedAnimation() {
    ValueAnimator animator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
    animator.setDuration(mAnimDuration);
    animator.setInterpolator(new LinearInterpolator());
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator animation) {
        mScaleVal = ((Float)animation.getAnimatedValue()).floatValue();
        mFloorColor = SmoothCheckBox.getGradientColor(mCheckedColor, SmoothCheckBox.COLOR_FLOOR_UNCHECKED, mScaleVal);
        postInvalidate();
      }
    });
    animator.start();

    ValueAnimator floorAnimator = ValueAnimator.ofFloat(new float[] { 1.0F, 0.8F, 1.0F });
    floorAnimator.setDuration(mAnimDuration);
    floorAnimator.setInterpolator(new LinearInterpolator());
    floorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator animation) {
        mFloorScale = ((Float)animation.getAnimatedValue()).floatValue();
        postInvalidate();
      }
    });
    floorAnimator.start();
  }

  private void drawTickDelayed() {
    postDelayed(new Runnable()
    {
      public void run() {
        mTickDrawing = true;
        postInvalidate(); } }, mAnimDuration);
  }


  private static int getGradientColor(int startColor, int endColor, float percent)
  {
    int sr = (startColor & 0xFF0000) >> 16;
    int sg = (startColor & 0xFF00) >> 8;
    int sb = startColor & 0xFF;

    int er = (endColor & 0xFF0000) >> 16;
    int eg = (endColor & 0xFF00) >> 8;
    int eb = endColor & 0xFF;

    int cr = (int)(sr * (1.0F - percent) + er * percent);
    int cg = (int)(sg * (1.0F - percent) + eg * percent);
    int cb = (int)(sb * (1.0F - percent) + eb * percent);
    return Color.argb(255, cr, cg, cb);
  }

  public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
    mListener = l;
  }




  public int dp2px(Context context, float dipValue)
  {
    float scale = context.getResources().getDisplayMetrics().density;
    return (int)(dipValue * scale + 0.5F);
  }

  public static abstract interface OnCheckedChangeListener
  {
    public abstract void onCheckedChanged(SmoothCheckBox paramSmoothCheckBox, boolean paramBoolean);
  }
}
