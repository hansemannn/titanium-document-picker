package droidninja.filepicker.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class SectionsPagerAdapter
  extends FragmentPagerAdapter
{
  private final List<Fragment> mFragmentList = new ArrayList();
  private final List<String> mFragmentTitles = new ArrayList();
  
  public SectionsPagerAdapter(FragmentManager fm) { super(fm); }
  

  public Fragment getItem(int position)
  {
    return (Fragment)mFragmentList.get(position);
  }
  

  public int getCount()
  {
    return mFragmentList.size();
  }
  
  public void addFragment(Fragment fragment, String title) {
    mFragmentList.add(fragment);
    mFragmentTitles.add(title);
  }
  
  public CharSequence getPageTitle(int position)
  {
    return (CharSequence)mFragmentTitles.get(position);
  }
}
