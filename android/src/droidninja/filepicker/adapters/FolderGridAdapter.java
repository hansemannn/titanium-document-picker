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
import android.widget.TextView;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.models.PhotoDirectory;
import droidninja.filepicker.utils.AndroidLifecycleUtils;
import ti.filepicker.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class FolderGridAdapter extends SelectableAdapter<FolderGridAdapter.PhotoViewHolder, PhotoDirectory>
{
  private final Context context;
  private final RequestManager glide;
  private final boolean showCamera;
  private int imageSize;
  public static final int ITEM_TYPE_CAMERA = 100;
  public static final int ITEM_TYPE_PHOTO = 101;
  private FolderGridAdapterListener folderGridAdapterListener;

  public FolderGridAdapter(Context context, RequestManager requestManager, ArrayList<PhotoDirectory> photos, ArrayList<String> selectedPaths, boolean showCamera)
  {
    super(photos, selectedPaths);
    this.context = context;
    glide = requestManager;
    this.showCamera = showCamera;
    setColumnNumber(context, 3);
  }

  public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
  {
    View itemView = LayoutInflater.from(context).inflate(Utils.getR("layout.item_folder_layout"), parent, false);

    return new PhotoViewHolder(itemView);
  }

  public int getItemViewType(int position)
  {
    if (showCamera) {
      return position == 0 ? 100 : 101;
    }
    return 101;
  }

  public void onBindViewHolder(PhotoViewHolder holder, int position)
  {
    if (getItemViewType(position) == 101)
    {
      final PhotoDirectory photoDirectory = (PhotoDirectory)getItems().get(showCamera ? position - 1 : position);

      if (AndroidLifecycleUtils.canLoadImage(holder.imageView.getContext()))
      {
        glide.load(new File(photoDirectory.getCoverPath())).apply(RequestOptions.centerCropTransform().override(imageSize, imageSize).placeholder(Utils.getR("drawable.image_placeholder"))).thumbnail(0.5F).into(holder.imageView);
      }

      holder.folderTitle.setText(photoDirectory.getName());
      holder.folderCount.setText(String.valueOf(photoDirectory.getMedias().size()));

      holder.itemView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View view) {
          if (folderGridAdapterListener != null)
            folderGridAdapterListener.onFolderClicked(photoDirectory);
        }
      });
      holder.bottomOverlay.setVisibility(0);
    }
    else
    {
    	holder.imageView.setImageResource(PickerManager.getInstance().getCameraDrawable());
    	holder.itemView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View view) {
          if (folderGridAdapterListener != null)
            folderGridAdapterListener.onCameraClicked();
        }
      });
    	holder.bottomOverlay.setVisibility(8);
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

  public void setFolderGridAdapterListener(FolderGridAdapterListener onClickListener)
  {
    folderGridAdapterListener = onClickListener;
  }

  public static class PhotoViewHolder extends RecyclerView.ViewHolder
  {
    ImageView imageView;
    TextView folderTitle;
    TextView folderCount;
    View bottomOverlay;
    View selectBg;

    public PhotoViewHolder(View itemView) {
      super(itemView);
      imageView = ((ImageView)itemView.findViewById(Utils.getR("id.iv_photo")));
      folderTitle = ((TextView)itemView.findViewById(Utils.getR("id.folder_title")));
      folderCount = ((TextView)itemView.findViewById(Utils.getR("id.folder_count")));
      bottomOverlay = itemView.findViewById(Utils.getR("id.bottomOverlay"));
      selectBg = itemView.findViewById(Utils.getR("id.transparent_bg"));
    }
  }

  public static abstract interface FolderGridAdapterListener
  {
    public abstract void onCameraClicked();

    public abstract void onFolderClicked(PhotoDirectory paramPhotoDirectory);
  }
}
