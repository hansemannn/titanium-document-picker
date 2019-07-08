package droidninja.filepicker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.models.Document;
import droidninja.filepicker.models.FileType;
import droidninja.filepicker.views.SmoothCheckBox;
import droidninja.filepicker.views.SmoothCheckBox.OnCheckedChangeListener;
import ti.filepicker.Utils;

import java.util.ArrayList;
import java.util.List;

public class FileListAdapter extends SelectableAdapter<FileListAdapter.FileViewHolder, Document> implements Filterable {
  private final Context context;
  private final FileAdapterListener mListener;
  private List<Document> mFilteredList;

  public FileListAdapter(Context context, List<Document> items, List<String> selectedPaths, FileAdapterListener fileAdapterListener)
  {
    super(items, selectedPaths);
    mFilteredList = items;
    this.context = context;
    mListener = fileAdapterListener;
  }

  public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(context).inflate(Utils.getR("layout.item_doc_layout"), parent, false);

    return new FileViewHolder(itemView);
  }
  
  public void onBindViewHolder(final FileViewHolder holder, int position) {
    final Document document = (Document)mFilteredList.get(position);

    int drawable = document.getFileType().getDrawable();
    holder.imageView.setImageResource(drawable);
    
    if ( (drawable == Utils.getR("drawable.icon_file_unknown")) || (drawable == Utils.getR("drawable.icon_file_pdf")) ) {
    	holder.fileTypeTv.setVisibility(0);
    	holder.fileTypeTv.setText(document.getFileType().title);
    } else {
    	holder.fileTypeTv.setVisibility(8);
    }

    holder.fileNameTextView.setText(document.getTitle());
    holder.fileSizeTextView.setText(
      Formatter.formatShortFileSize(context, Long.parseLong(document.getSize())));

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        FileListAdapter.this.onItemClicked(document, holder);
      }


    });
    holder.checkBox.setOnCheckedChangeListener(null);
    holder.checkBox.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        FileListAdapter.this.onItemClicked(document, holder);
      }


    });
    holder.checkBox.setChecked(isSelected(document));

    holder.itemView.setBackgroundResource(
      isSelected(document) ? Utils.getR("color.bg_gray") : 17170443);
    holder.checkBox.setVisibility(isSelected(document) ? 0 : 8);

    holder.checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
      public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
        toggleSelection(document);
        holder.itemView.setBackgroundResource(isChecked ? Utils.getR("color.bg_gray") : 17170443);
      }
    });
  }

  private void onItemClicked(Document document, FileViewHolder holder) {
    if (PickerManager.getInstance().getMaxCount() == 1) {
      PickerManager.getInstance().add(document.getPath(), 2);
    }
    else if (holder.checkBox.isChecked()) {
      PickerManager.getInstance().remove(document.getPath(), 2);
      holder.checkBox.setChecked(!holder.checkBox.isChecked(), true);
      holder.checkBox.setVisibility(8);
    } else if (PickerManager.getInstance().shouldAdd()) {
      PickerManager.getInstance().add(document.getPath(), 2);
      holder.checkBox.setChecked(!holder.checkBox.isChecked(), true);
      holder.checkBox.setVisibility(0);
    }


    if (mListener != null) mListener.onItemSelected();
  }

  public int getItemCount() {
    return mFilteredList.size();
  }

  public Filter getFilter() {
    return new Filter()
    {
      protected Filter.FilterResults performFiltering(CharSequence charSequence) {
        String charString = charSequence.toString();

        if (charString.isEmpty())
        {
          mFilteredList = getItems();
        }
        else {
          ArrayList<Document> filteredList = new ArrayList();

          for (Document document : getItems())
          {
            if (document.getTitle().toLowerCase().contains(charString))
            {
              filteredList.add(document);
            }
          }

          mFilteredList = filteredList;
        }

        Filter.FilterResults filterResults = new Filter.FilterResults();
        filterResults.values = mFilteredList;
        return filterResults;
      }

      protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults)
      {
        mFilteredList = ((ArrayList) filterResults.values);
        notifyDataSetChanged();
      }
    };
  }


  public static class FileViewHolder extends RecyclerView.ViewHolder
  {
    TextView fileTypeTv;
    SmoothCheckBox checkBox;
    ImageView imageView;
    TextView fileNameTextView;
    TextView fileSizeTextView;

    public FileViewHolder(View itemView)
    {
      super(itemView);
      checkBox = ((SmoothCheckBox)itemView.findViewById(Utils.getR("id.checkbox")));
      imageView = ((ImageView)itemView.findViewById(Utils.getR("id.file_iv")));
      fileNameTextView = ((TextView)itemView.findViewById(Utils.getR("id.file_name_tv")));
      fileTypeTv = ((TextView)itemView.findViewById(Utils.getR("id.file_type_tv")));
      fileSizeTextView = ((TextView)itemView.findViewById(Utils.getR("id.file_size_tv")));
    }
  }
}
