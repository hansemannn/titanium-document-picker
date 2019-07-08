package droidninja.filepicker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.adapters.SectionsPagerAdapter;
import ti.filepicker.Utils;




public class MediaPickerFragment
  extends BaseFragment
{
  TabLayout tabLayout;
  ViewPager viewPager;
  private MediaPickerFragmentListener mListener;

  public MediaPickerFragment() {}

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    return inflater.inflate(Utils.getR("layout.fragment_media_picker"), container, false);
  }

  public void onAttach(Context context)
  {
    super.onAttach(context);
    if ((context instanceof MediaPickerFragmentListener)) {
      mListener = ((MediaPickerFragmentListener)context);
    } else {
      throw new RuntimeException(context.toString() + " must implement MediaPickerFragment");
    }
  }


  public void onDetach()
  {
    super.onDetach();
    mListener = null;
  }

  public static MediaPickerFragment newInstance() {
    MediaPickerFragment photoPickerFragment = new MediaPickerFragment();
    return photoPickerFragment;
  }




  public void onCreate(@Nullable Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
  }


  public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);
    initView(view);
  }

  private void initView(View view) {
    tabLayout = ((TabLayout)view.findViewById(Utils.getR("id.tabs")));
    viewPager = ((ViewPager)view.findViewById(Utils.getR("id.viewPager")));
    tabLayout.setTabGravity(0);
    tabLayout.setTabMode(1);

    SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());

    if (PickerManager.getInstance().showImages()) {
      if (PickerManager.getInstance().isShowFolderView()) {
        adapter.addFragment(MediaFolderPickerFragment.newInstance(1), getString(Utils.getR("string.images")));
      } else {
        adapter.addFragment(MediaDetailPickerFragment.newInstance(1), getString(Utils.getR("string.images")));
      }
    } else {
      tabLayout.setVisibility(8);
    }
    if (PickerManager.getInstance().showVideo())
    {
      if (PickerManager.getInstance().isShowFolderView()) {
        adapter.addFragment(MediaFolderPickerFragment.newInstance(3), getString(Utils.getR("string.videos")));
      } else {
        adapter.addFragment(MediaDetailPickerFragment.newInstance(3), getString(Utils.getR("string.videos")));
      }
    } else {
      tabLayout.setVisibility(8);
    }
    viewPager.setAdapter(adapter);
    tabLayout.setupWithViewPager(viewPager);
  }

  public static abstract interface MediaPickerFragmentListener {}
}
