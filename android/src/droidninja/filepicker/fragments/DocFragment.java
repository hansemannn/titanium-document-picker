package droidninja.filepicker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.adapters.FileListAdapter;
import droidninja.filepicker.models.Document;
import droidninja.filepicker.models.FileType;
import ti.filepicker.Utils;

import java.util.List;

public class DocFragment extends BaseFragment implements droidninja.filepicker.adapters.FileAdapterListener
{
  private static final String TAG = DocFragment.class.getSimpleName();

  RecyclerView recyclerView;

  TextView emptyView;

  private DocFragmentListener mListener;

  private MenuItem selectAllItem;

  private FileListAdapter fileListAdapter;

  public DocFragment() {}

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    return inflater.inflate(Utils.getR("layout.fragment_photo_picker"), container, false);
  }

  public void onAttach(Context context) {
    super.onAttach(context);
    if ((context instanceof DocFragmentListener)) {
      mListener = ((DocFragmentListener)context);
    }
    else {
      throw new RuntimeException(context.toString() + " must implement PhotoPickerFragmentListener");
    }
  }

  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  public static DocFragment newInstance(FileType fileType) {
    DocFragment photoPickerFragment = new DocFragment();
    Bundle bun = new Bundle();
    bun.putParcelable("FILE_TYPE", fileType);
    photoPickerFragment.setArguments(bun);
    return photoPickerFragment;
  }

  public FileType getFileType() {
    return (FileType)getArguments().getParcelable("FILE_TYPE");
  }

  public void onItemSelected() {
    mListener.onItemSelected();
    if ((fileListAdapter != null) && (selectAllItem != null) &&
      (fileListAdapter.getItemCount() == fileListAdapter.getSelectedItemCount())) {
      selectAllItem.setIcon(Utils.getR("drawable.ic_select_all"));
      selectAllItem.setChecked(true);
    }
  }





  public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);
    initView(view);
  }

  private void initView(View view) {
    recyclerView = ((RecyclerView)view.findViewById(Utils.getR("id.recyclerview")));
    emptyView = ((TextView)view.findViewById(Utils.getR("id.empty_view")));
    recyclerView.setLayoutManager(new android.support.v7.widget.LinearLayoutManager(getActivity()));
    recyclerView.setVisibility(8);
  }

  public void updateList(List<Document> dirs) {
    if (getView() == null) { return;
    }
    if (dirs.size() > 0) {
      recyclerView.setVisibility(0);
      emptyView.setVisibility(8);

      fileListAdapter = ((FileListAdapter)recyclerView.getAdapter());
      if (fileListAdapter == null)
      {
        fileListAdapter = new FileListAdapter(getActivity(), dirs, PickerManager.getInstance().getSelectedFiles(), this);


        recyclerView.setAdapter(fileListAdapter);
      } else {
        fileListAdapter.setData(dirs);
        fileListAdapter.notifyDataSetChanged();
      }
      onItemSelected();
    } else {
      recyclerView.setVisibility(8);
      emptyView.setVisibility(0);
    }
  }

  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(Utils.getR("menu.doc_picker_menu"), menu);
    selectAllItem = menu.findItem(Utils.getR("id.action_select"));
    if (PickerManager.getInstance().hasSelectAll()) {
      selectAllItem.setVisible(true);
      onItemSelected();
    } else {
      selectAllItem.setVisible(false);
    }

    MenuItem search = menu.findItem(Utils.getR("id.search"));
    SearchView searchView = (SearchView)search.getActionView();
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
    {
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      public boolean onQueryTextChange(String newText) {
        if (fileListAdapter != null) {
          fileListAdapter.getFilter().filter(newText);
        }
        return true;
      }

    });
    super.onCreateOptionsMenu(menu, inflater);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == Utils.getR("id.action_select")) {
      if (fileListAdapter != null) {
        if (selectAllItem != null) {
          if (selectAllItem.isChecked()) {
            fileListAdapter.clearSelection();
            PickerManager.getInstance().clearSelections();

            selectAllItem.setIcon(Utils.getR("drawable.ic_deselect_all"));
          } else {
            fileListAdapter.selectAll();
            PickerManager.getInstance()
              .add(fileListAdapter.getSelectedPaths(), 2);
            selectAllItem.setIcon(Utils.getR("drawable.ic_select_all"));
          }
        }
        selectAllItem.setChecked(!selectAllItem.isChecked());
        mListener.onItemSelected();
      }
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public static abstract interface DocFragmentListener
  {
    public abstract void onItemSelected();
  }
}
