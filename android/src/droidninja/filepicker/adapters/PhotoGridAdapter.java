package droidninja.filepicker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.models.Media;
import droidninja.filepicker.utils.AndroidLifecycleUtils;
import droidninja.filepicker.views.SmoothCheckBox;
import droidninja.filepicker.views.SmoothCheckBox.OnCheckedChangeListener;
import ti.filepicker.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class PhotoGridAdapter extends SelectableAdapter<PhotoGridAdapter.PhotoViewHolder, Media>
{
  private final Context context;
  private final RequestManager glide;
  private final boolean showCamera;
  private final FileAdapterListener mListener;
  private int imageSize;
  public static final int ITEM_TYPE_CAMERA = 100;
  public static final int ITEM_TYPE_PHOTO = 101;
  private View.OnClickListener cameraOnClickListener;

  public PhotoGridAdapter(Context context, RequestManager requestManager, ArrayList<Media> medias, ArrayList<String> selectedPaths, boolean showCamera, FileAdapterListener photoGridAdapterListener)
  {
    super(medias, selectedPaths);
    this.context = context;
    glide = requestManager;
    this.showCamera = showCamera;
    mListener = photoGridAdapterListener;
    setColumnNumber(context, 3);
  }

  public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
  {
    View itemView = LayoutInflater.from(context).inflate(Utils.getR("layout.item_photo_layout"), parent, false);
    return new PhotoViewHolder(itemView);
  }

  public int getItemViewType(int position)
  {
    if (showCamera) {
      return position == 0 ? 100 : 101;
    }
    return 101;
  }

  public void onBindViewHolder(final PhotoViewHolder holder, int position)
  {
    if (getItemViewType(position) == 101)
    {
      final Media media = (Media)getItems().get(showCamera ? position - 1 : position);

      if (AndroidLifecycleUtils.canLoadImage(holder.imageView.getContext()))
      {
        glide.load(new File(media.getPath())).apply(RequestOptions.centerCropTransform().override(imageSize, imageSize).placeholder(Utils.getR("drawable.image_placeholder"))).thumbnail(0.5F).into(holder.imageView);
      }


      if (media.getMediaType() == 3) {
    	  holder.videoIcon.setVisibility(0);
      } else {
    	  holder.videoIcon.setVisibility(8);
      }
      holder.itemView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View v) {
          PhotoGridAdapter.this.onItemClicked(holder, media);
        }


      });
      holder.checkBox.setVisibility(8);
      holder.checkBox.setOnCheckedChangeListener(null);
      holder.checkBox.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View view) {
          PhotoGridAdapter.this.onItemClicked(holder, media);
        }


      });
      holder.checkBox.setChecked(isSelected(media));
      holder.selectBg.setVisibility(isSelected(media) ? 0 : 8);
      holder.checkBox.setVisibility(isSelected(media) ? 0 : 8);
      holder.checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener()
      {
        public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
          toggleSelection(media);
          holder.selectBg.setVisibility(isChecked ? 0 : 8);

          if (isChecked)
          {
            holder.checkBox.setVisibility(0);
            PickerManager.getInstance().add(media.getPath(), 1);
          }
          else
          {
            holder.checkBox.setVisibility(8);
            PickerManager.getInstance().remove(media.getPath(), 1);
          }

          if (mListener != null) {
            mListener.onItemSelected();
          }
        }
      });
    }
    else
    {
    	holder.imageView.setImageResource(PickerManager.getInstance().getCameraDrawable());
    	holder.checkBox.setVisibility(8);
    	holder.itemView.setOnClickListener(cameraOnClickListener);
    	holder.videoIcon.setVisibility(8);
    }
  }

  private void onItemClicked(PhotoViewHolder holder, Media media) {
    if (PickerManager.getInstance().getMaxCount() == 1)
    {
      PickerManager.getInstance().add(media.getPath(), 1);
      if (mListener != null) {
        mListener.onItemSelected();
      }
    } else if ((holder.checkBox.isChecked()) || (PickerManager.getInstance().shouldAdd())) {
    	holder.checkBox.setChecked(!holder.checkBox.isChecked(), true);
    }
  }

  private void setColumnNumber(Context context, int columnNum) {
    WindowManager wm = (WindowManager)context.getSystemService("window");
    DisplayMetrics metrics = new DisplayMetrics();
    wm.getDefaultDisplay().getMetrics(metrics);
    int widthPixels = metrics.widthPixels;
    imageSize = (widthPixels / columnNum);
  }

  public int getItemCount()
  {
    if (showCamera)
      return getItems().size() + 1;
    return getItems().size();
  }

  public void setCameraListener(View.OnClickListener onClickListener)
  {
    cameraOnClickListener = onClickListener;
  }


  public static class PhotoViewHolder extends RecyclerView.ViewHolder
  {
    SmoothCheckBox checkBox;
    ImageView imageView;
    ImageView videoIcon;
    View selectBg;

    public PhotoViewHolder(View itemView)
    {
      super(itemView);
      checkBox = ((SmoothCheckBox)itemView.findViewById(Utils.getR("id.checkbox") ) );
      imageView = ((ImageView)itemView.findViewById( Utils.getR("id.iv_photo") ) );
      videoIcon = ((ImageView)itemView.findViewById( Utils.getR("id.video_icon") ) );
      selectBg = itemView.findViewById(Utils.getR("id.transparent_bg"));
    }
  }
}
