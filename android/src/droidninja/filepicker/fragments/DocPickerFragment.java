package droidninja.filepicker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.adapters.SectionsPagerAdapter;
import droidninja.filepicker.cursors.loadercallbacks.FileMapResultCallback;
import droidninja.filepicker.models.Document;
import droidninja.filepicker.models.FileType;
import droidninja.filepicker.models.sort.SortingTypes;
import droidninja.filepicker.utils.TabLayoutHelper;
import ti.filepicker.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocPickerFragment extends BaseFragment
{
  private static final String TAG = DocPickerFragment.class.getSimpleName();

  TabLayout tabLayout;

  ViewPager viewPager;

  private ProgressBar progressBar;

  private DocPickerFragmentListener mListener;


  public DocPickerFragment() {}


  public void onCreate(@Nullable Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }



  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    return inflater.inflate(Utils.getR("layout.fragment_doc_picker"), container, false);
  }

  public static DocPickerFragment newInstance() {
    DocPickerFragment docPickerFragment = new DocPickerFragment();
    return docPickerFragment;
  }

  public void onAttach(Context context)
  {
    super.onAttach(context);
    if ((context instanceof DocPickerFragmentListener)) {
      mListener = ((DocPickerFragmentListener)context);
    } else {
      throw new RuntimeException(context.toString() + " must implement DocPickerFragmentListener");
    }
  }


  public void onDetach()
  {
    super.onDetach();
    mListener = null;
  }

  public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);
    setViews(view);
    initView();
  }

  private void initView() {
    setUpViewPager();
    setData();
  }

  private void setViews(View view) {
    tabLayout = ((TabLayout)view.findViewById(Utils.getR("id.tabs")));
    viewPager = ((ViewPager)view.findViewById(Utils.getR("id.viewPager")));
    progressBar = ((ProgressBar)view.findViewById(Utils.getR("id.progress_bar")));

    tabLayout.setTabGravity(0);
    tabLayout.setTabMode(0);
  }

  private void setData() {
    droidninja.filepicker.utils.MediaStoreHelper.getDocs(getActivity(),
      PickerManager.getInstance().getFileTypes(),
      PickerManager.getInstance().getSortingType().getComparator(), new FileMapResultCallback()
      {
        public void onResultCallback(Map<FileType, List<Document>> files)
        {
          if (!isAdded()) return;
          progressBar.setVisibility(8);
          DocPickerFragment.this.setDataOnFragments(files);
        }
      });
  }

  private void setDataOnFragments(Map<FileType, List<Document>> filesMap)
  {
    SectionsPagerAdapter sectionsPagerAdapter = (SectionsPagerAdapter)viewPager.getAdapter();
    if (sectionsPagerAdapter != null)
    {
      for (int index = 0; index < sectionsPagerAdapter.getCount(); index++)
      {
        DocFragment docFragment = (DocFragment)getChildFragmentManager().findFragmentByTag("android:switcher:" + Utils.getR("id.viewPager") + ":" + index);

        if (docFragment != null)
        {
          FileType fileType = docFragment.getFileType();
          if (fileType != null) {
            List<Document> filesFilteredByType = (List)filesMap.get(fileType);
            if (filesFilteredByType != null)
              docFragment.updateList(filesFilteredByType);
          }
        }
      }
    }
  }

  private void setUpViewPager() {
    SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());
    ArrayList<FileType> supportedTypes = PickerManager.getInstance().getFileTypes();
    for (int index = 0; index < supportedTypes.size(); index++) {
      adapter.addFragment(DocFragment.newInstance((FileType)supportedTypes.get(index)), supportedTypes.get(index).title);
    }

    viewPager.setOffscreenPageLimit(supportedTypes.size());
    viewPager.setAdapter(adapter);
    tabLayout.setupWithViewPager(viewPager);

    TabLayoutHelper mTabLayoutHelper = new TabLayoutHelper(tabLayout, viewPager);
    mTabLayoutHelper.setAutoAdjustTabModeEnabled(true);
  }

  public static abstract interface DocPickerFragmentListener {}
}
