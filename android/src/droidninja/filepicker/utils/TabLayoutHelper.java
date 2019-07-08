package droidninja.filepicker.utils;

import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnAdapterChangeListener;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;



public class TabLayoutHelper
{
	protected TabLayout mTabLayout;
  protected ViewPager mViewPager;
  protected TabLayout.OnTabSelectedListener mUserOnTabSelectedListener;
  protected TabLayout.OnTabSelectedListener mInternalOnTabSelectedListener;
  protected FixedTabLayoutOnPageChangeListener mInternalTabLayoutOnPageChangeListener;
  protected OnAdapterChangeListener mInternalOnAdapterChangeListener;
  protected DataSetObserver mInternalDataSetObserver;
  protected Runnable mAdjustTabModeRunnable;
  protected Runnable mSetTabsFromPagerAdapterRunnable;
  protected Runnable mUpdateScrollPositionRunnable;
  protected boolean mAutoAdjustTabMode = false;



  protected boolean mDuringSetTabsFromPagerAdapter;



  public TabLayoutHelper(@NonNull TabLayout tabLayout, @NonNull ViewPager viewPager)
  {
    PagerAdapter adapter = viewPager.getAdapter();

    if (adapter == null) {
      throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
    }

    mTabLayout = tabLayout;
    mViewPager = viewPager;

    mInternalDataSetObserver = new DataSetObserver()
    {
      public void onChanged() {
        handleOnDataSetChanged();
      }

    };
    mInternalOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
      public void onTabSelected(TabLayout.Tab tab) {
        handleOnTabSelected(tab);
      }

      public void onTabUnselected(TabLayout.Tab tab)
      {
        handleOnTabUnselected(tab);
      }

      public void onTabReselected(TabLayout.Tab tab)
      {
        handleOnTabReselected(tab);
      }

    };
    mInternalTabLayoutOnPageChangeListener = new FixedTabLayoutOnPageChangeListener(mTabLayout);

    mInternalOnAdapterChangeListener = new ViewPager.OnAdapterChangeListener()
    {
      public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
        handleOnAdapterChanged(viewPager, oldAdapter, newAdapter);
      }

    };
    setupWithViewPager(mTabLayout, mViewPager);
  }









  public TabLayout getTabLayout()
  {
    return mTabLayout;
  }





  public ViewPager getViewPager()
  {
    return mViewPager;
  }





  public void setAutoAdjustTabModeEnabled(boolean enabled)
  {
    if (mAutoAdjustTabMode == enabled) {
      return;
    }
    mAutoAdjustTabMode = enabled;

    if (mAutoAdjustTabMode) {
      adjustTabMode(-1);
    } else {
      cancelPendingAdjustTabMode();
    }
  }





  public boolean isAutoAdjustTabModeEnabled()
  {
    return mAutoAdjustTabMode;
  }






  @Deprecated
  public void setOnTabSelectedListener(TabLayout.OnTabSelectedListener listener)
  {
    mUserOnTabSelectedListener = listener;
  }




  public void release()
  {
    cancelPendingAdjustTabMode();
    cancelPendingSetTabsFromPagerAdapter();
    cancelPendingUpdateScrollPosition();

    if (mInternalOnAdapterChangeListener != null) {
      mViewPager.removeOnAdapterChangeListener(mInternalOnAdapterChangeListener);
      mInternalOnAdapterChangeListener = null;
    }
    if (mInternalDataSetObserver != null) {
      mViewPager.getAdapter().unregisterDataSetObserver(mInternalDataSetObserver);
      mInternalDataSetObserver = null;
    }
    if (mInternalOnTabSelectedListener != null) {
      mTabLayout.removeOnTabSelectedListener(mInternalOnTabSelectedListener);
      mInternalOnTabSelectedListener = null;
    }
    if (mInternalTabLayoutOnPageChangeListener != null) {
      mViewPager.removeOnPageChangeListener(mInternalTabLayoutOnPageChangeListener);
      mInternalTabLayoutOnPageChangeListener = null;
    }
    mUserOnTabSelectedListener = null;
    mViewPager = null;
    mTabLayout = null;
  }

  public void updateAllTabs() {
    int count = mTabLayout.getTabCount();
    for (int i = 0; i < count; i++) {
      updateTab(mTabLayout.getTabAt(i));
    }
  }








  protected TabLayout.Tab onCreateTab(TabLayout tabLayout, PagerAdapter adapter, int position)
  {
    TabLayout.Tab tab = tabLayout.newTab();
    tab.setText(adapter.getPageTitle(position));
    return tab;
  }





  protected void onUpdateTab(TabLayout.Tab tab)
  {
    if (tab.getCustomView() == null) {
      tab.setCustomView(null);
    }
  }



  protected void handleOnDataSetChanged()
  {
    cancelPendingUpdateScrollPosition();
    cancelPendingSetTabsFromPagerAdapter();

    if (mSetTabsFromPagerAdapterRunnable == null) {
      mSetTabsFromPagerAdapterRunnable = new Runnable()
      {
        public void run() {
          setTabsFromPagerAdapter(mTabLayout, mViewPager.getAdapter(), mViewPager.getCurrentItem());
        }
      };
    }

    mTabLayout.post(mSetTabsFromPagerAdapterRunnable);
  }

  protected void handleOnTabSelected(TabLayout.Tab tab) {
    if (mDuringSetTabsFromPagerAdapter) {
      return;
    }
    mViewPager.setCurrentItem(tab.getPosition());
    cancelPendingUpdateScrollPosition();

    if (mUserOnTabSelectedListener != null) {
      mUserOnTabSelectedListener.onTabSelected(tab);
    }
  }

  protected void handleOnTabUnselected(TabLayout.Tab tab) {
    if (mDuringSetTabsFromPagerAdapter) {
      return;
    }
    if (mUserOnTabSelectedListener != null) {
      mUserOnTabSelectedListener.onTabUnselected(tab);
    }
  }

  protected void handleOnTabReselected(TabLayout.Tab tab) {
    if (mDuringSetTabsFromPagerAdapter) {
      return;
    }
    if (mUserOnTabSelectedListener != null) {
      mUserOnTabSelectedListener.onTabReselected(tab);
    }
  }

  protected void handleOnAdapterChanged(ViewPager viewPager, PagerAdapter oldAdapter, PagerAdapter newAdapter) {
    if (mViewPager != viewPager) {
      return;
    }

    if (oldAdapter != null) {
      oldAdapter.unregisterDataSetObserver(mInternalDataSetObserver);
    }
    if (newAdapter != null) {
      newAdapter.registerDataSetObserver(mInternalDataSetObserver);
    }

    setTabsFromPagerAdapter(mTabLayout, newAdapter, mViewPager.getCurrentItem());
  }

  protected void cancelPendingAdjustTabMode() {
    if (mAdjustTabModeRunnable != null) {
      mTabLayout.removeCallbacks(mAdjustTabModeRunnable);
      mAdjustTabModeRunnable = null;
    }
  }

  protected void cancelPendingSetTabsFromPagerAdapter() {
    if (mSetTabsFromPagerAdapterRunnable != null) {
      mTabLayout.removeCallbacks(mSetTabsFromPagerAdapterRunnable);
      mSetTabsFromPagerAdapterRunnable = null;
    }
  }

  protected void cancelPendingUpdateScrollPosition() {
    if (mUpdateScrollPositionRunnable != null) {
      mTabLayout.removeCallbacks(mUpdateScrollPositionRunnable);
      mUpdateScrollPositionRunnable = null;
    }
  }

  protected void adjustTabMode(int prevScrollX) {
    if (mAdjustTabModeRunnable != null) {
      return;
    }

    if (prevScrollX < 0) {
      prevScrollX = mTabLayout.getScrollX();
    }

    if (ViewCompat.isLaidOut(mTabLayout)) {
      adjustTabModeInternal(mTabLayout, prevScrollX);
    } else {
      final int prevScrollX1 = prevScrollX;
      mAdjustTabModeRunnable = new Runnable()
      {
        public void run() {
          mAdjustTabModeRunnable = null;
          adjustTabModeInternal(mTabLayout, prevScrollX1);
        }
      };
      mTabLayout.post(mAdjustTabModeRunnable);
    }
  }

  protected TabLayout.Tab createNewTab(TabLayout tabLayout, PagerAdapter adapter, int position) {
    return onCreateTab(tabLayout, adapter, position);
  }

  protected void setupWithViewPager(@NonNull TabLayout tabLayout, @NonNull ViewPager viewPager) {
    PagerAdapter adapter = viewPager.getAdapter();
    if (adapter == null) {
      throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
    }

    setTabsFromPagerAdapter(tabLayout, adapter, viewPager.getCurrentItem());

    viewPager.getAdapter().registerDataSetObserver(mInternalDataSetObserver);

    viewPager.addOnPageChangeListener(mInternalTabLayoutOnPageChangeListener);
    viewPager.addOnAdapterChangeListener(mInternalOnAdapterChangeListener);

    tabLayout.addOnTabSelectedListener(mInternalOnTabSelectedListener);
  }

  protected void setTabsFromPagerAdapter(@NonNull TabLayout tabLayout, @Nullable PagerAdapter adapter, int currentItem) {
    try {
      mDuringSetTabsFromPagerAdapter = true;

      int prevSelectedTab = tabLayout.getSelectedTabPosition();
      int prevScrollX = tabLayout.getScrollX();


      tabLayout.removeAllTabs();


      if (adapter != null) {
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
          TabLayout.Tab tab = createNewTab(tabLayout, adapter, i);
          tabLayout.addTab(tab, false);
          updateTab(tab);
        }


        currentItem = Math.min(currentItem, count - 1);
        if (currentItem >= 0) {
          tabLayout.getTabAt(currentItem).select();
        }
      }


      if (mAutoAdjustTabMode) {
        adjustTabMode(prevScrollX);
      }
      else {
        int curTabMode = tabLayout.getTabMode();
        if (curTabMode == 0) {
          tabLayout.scrollTo(prevScrollX, 0);
        }
      }
    } finally {
      mDuringSetTabsFromPagerAdapter = false;
    }
  }

  protected void updateTab(TabLayout.Tab tab) {
    onUpdateTab(tab);
  }

  protected int determineTabMode(@NonNull TabLayout tabLayout) {
    LinearLayout slidingTabStrip = (LinearLayout)tabLayout.getChildAt(0);

    int childCount = slidingTabStrip.getChildCount();




    int tabLayoutWidth = tabLayout.getMeasuredWidth() - tabLayout.getPaddingLeft() - tabLayout.getPaddingRight();
    int tabLayoutHeight = tabLayout.getMeasuredHeight() - tabLayout.getPaddingTop() - tabLayout.getPaddingBottom();

    if (childCount == 0) {
      return 1;
    }

    int stripWidth = 0;
    int maxWidthTab = 0;
    int tabHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(tabLayoutHeight, 1073741824);

    for (int i = 0; i < childCount; i++) {
      View tabView = slidingTabStrip.getChildAt(i);
      tabView.measure(0, tabHeightMeasureSpec);
      int tabWidth = tabView.getMeasuredWidth();
      stripWidth += tabWidth;
      maxWidthTab = Math.max(maxWidthTab, tabWidth);
    }

    return (stripWidth < tabLayoutWidth) && (maxWidthTab < tabLayoutWidth / childCount) ? 1 : 0;
  }

  protected void adjustTabModeInternal(@NonNull TabLayout tabLayout, int prevScrollX)
  {
    int prevTabMode = tabLayout.getTabMode();

    tabLayout.setTabMode(0);
    tabLayout.setTabGravity(1);

    int newTabMode = determineTabMode(tabLayout);

    cancelPendingUpdateScrollPosition();

    if (newTabMode == 1) {
      tabLayout.setTabGravity(0);
      tabLayout.setTabMode(1);
    } else {
      LinearLayout slidingTabStrip = (LinearLayout)tabLayout.getChildAt(0);
      slidingTabStrip.setGravity(1);
      if (prevTabMode == 0)
      {
        tabLayout.scrollTo(prevScrollX, 0);
      }
      else {
        mUpdateScrollPositionRunnable = new Runnable()
        {
          public void run() {
            mUpdateScrollPositionRunnable = null;
            TabLayoutHelper.this.updateScrollPosition();
          }
        };
        mTabLayout.post(mUpdateScrollPositionRunnable);
      }
    }
  }

  private void updateScrollPosition() {
    mTabLayout.setScrollPosition(mTabLayout.getSelectedTabPosition(), 0.0F, false);
  }

  protected static class FixedTabLayoutOnPageChangeListener implements ViewPager.OnPageChangeListener {
    private final WeakReference<TabLayout> mTabLayoutRef;
    private int mPreviousScrollState;
    private int mScrollState;

    public FixedTabLayoutOnPageChangeListener(TabLayout tabLayout) {
      mTabLayoutRef = new WeakReference(tabLayout);
    }

    public void onPageScrollStateChanged(int state)
    {
      mPreviousScrollState = mScrollState;
      mScrollState = state;
    }


    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
      TabLayout tabLayout = (TabLayout)mTabLayoutRef.get();
      if ((tabLayout != null) &&
        (shouldUpdateScrollPosition()))
      {

        boolean updateText = (mScrollState == 1) || ((mScrollState == 2) && (mPreviousScrollState == 1));


        tabLayout.setScrollPosition(position, positionOffset, updateText);
      }
    }


    public void onPageSelected(int position)
    {
      TabLayout tabLayout = (TabLayout)mTabLayoutRef.get();
      if ((tabLayout != null) && (tabLayout.getSelectedTabPosition() != position))
      {

        TabLayoutHelper.Internal.selectTab(tabLayout, tabLayout.getTabAt(position), mScrollState == 0);
      }
    }

    private boolean shouldUpdateScrollPosition()
    {
      return (mScrollState == 1) || ((mScrollState == 2) && (mPreviousScrollState == 1));
    }
  }





  static class Internal
  {
    private static final Method mMethodSelectTab = getAccessiblePrivateMethod(TabLayout.class, "selectTab", new Class[] { TabLayout.Tab.class, Boolean.TYPE });

    Internal() {}

    private static Method getAccessiblePrivateMethod(Class<?> targetClass, String methodName, Class<?>... params) throws RuntimeException {
      try { Method m = targetClass.getDeclaredMethod(methodName, params);
        m.setAccessible(true);
        return m;
      } catch (NoSuchMethodException e) {
        throw new IllegalStateException(e);
      }
    }

    public static void selectTab(TabLayout tabLayout, TabLayout.Tab tab, boolean updateIndicator) {
      try {
        mMethodSelectTab.invoke(tabLayout, new Object[] { tab, Boolean.valueOf(updateIndicator) });
      } catch (IllegalAccessException e) {
        new IllegalStateException(e);
      } catch (InvocationTargetException e) {
        throw handleInvocationTargetException(e);
      }
    }

    private static RuntimeException handleInvocationTargetException(InvocationTargetException e) {
      Throwable targetException = e.getTargetException();
      if ((targetException instanceof RuntimeException)) {
        throw ((RuntimeException)targetException);
      }
      throw new IllegalStateException(targetException);
    }
  }
}
