package droidninja.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import droidninja.filepicker.models.FileType;
import droidninja.filepicker.models.sort.SortingTypes;
import droidninja.filepicker.utils.Orientation;
import ti.filepicker.Utils;

import java.util.ArrayList;


public class FilePickerBuilder {
    private final Bundle mPickerOptionsBundle;

    public FilePickerBuilder() {
        mPickerOptionsBundle = new Bundle();
    }

    public static FilePickerBuilder getInstance() {
        return new FilePickerBuilder();
    }

    public FilePickerBuilder setMaxCount(int maxCount) {
        PickerManager.getInstance().setMaxCount(maxCount);
        return this;
    }

    public FilePickerBuilder setActivityTheme(int theme) {
        PickerManager.getInstance().setTheme(theme);
        return this;
    }

    public FilePickerBuilder setActivityTitle(String title) {
        PickerManager.getInstance().setTitle(title);
        return this;
    }

    public FilePickerBuilder setSelectedFiles(ArrayList < String > selectedPhotos) {
        mPickerOptionsBundle.putStringArrayList("SELECTED_PHOTOS", selectedPhotos);
        return this;
    }

    public FilePickerBuilder enableVideoPicker(boolean status) {
        PickerManager.getInstance().setShowVideos(status);
        return this;
    }

    public FilePickerBuilder enableImagePicker(boolean status) {
        PickerManager.getInstance().setShowImages(status);
        return this;
    }

    public FilePickerBuilder enableSelectAll(boolean status) {
        PickerManager.getInstance().enableSelectAll(status);
        return this;
    }

    public FilePickerBuilder setCameraPlaceholder(@DrawableRes int drawable) {
        PickerManager.getInstance().setCameraDrawable(drawable);
        return this;
    }

    public FilePickerBuilder showGifs(boolean status) {
        PickerManager.getInstance().setShowGif(status);
        return this;
    }

    public FilePickerBuilder showFolderView(boolean status) {
        PickerManager.getInstance().setShowFolderView(status);
        return this;
    }

    public FilePickerBuilder enableDocSupport(boolean status) {
        PickerManager.getInstance().setDocSupport(status);
        return this;
    }

    public FilePickerBuilder enableCameraSupport(boolean status) {
        PickerManager.getInstance().setEnableCamera(status);
        return this;
    }

    public FilePickerBuilder withOrientation(Orientation orientation) {
        PickerManager.getInstance().setOrientation(orientation);
        return this;
    }

    public FilePickerBuilder addFileSupport(String title, String[] extensions, @DrawableRes int drawable) {
        PickerManager.getInstance().addFileType(new FileType(title, extensions, drawable));
        return this;
    }

    public FilePickerBuilder addFileSupport(String title, String[] extensions) {
        PickerManager.getInstance().addFileType(new FileType(title, extensions, 0));
        return this;
    }

    public FilePickerBuilder sortDocumentsBy(SortingTypes type) {
        PickerManager.getInstance().setSortingType(type);
        return this;
    }

    public Intent pickPhoto(Activity context) {
        mPickerOptionsBundle.putInt("EXTRA_PICKER_TYPE", 17);
        return start(context, 233);
    }

    public void pickPhoto(Fragment context) {
        mPickerOptionsBundle.putInt("EXTRA_PICKER_TYPE", 17);
        start(context, 233);
    }

    public Intent pickFile(Activity context) {
        mPickerOptionsBundle.putInt("EXTRA_PICKER_TYPE", 18);
        return start(context, 234);
    }

    public void pickFile(Fragment context) {
        mPickerOptionsBundle.putInt("EXTRA_PICKER_TYPE", 18);
        start(context, 234);
    }

    public Intent pickPhoto(Activity context, int requestCode) {
        mPickerOptionsBundle.putInt("EXTRA_PICKER_TYPE", 17);
        return start(context, requestCode);
    }

    public void pickPhoto(Fragment context, int requestCode) {
        mPickerOptionsBundle.putInt("EXTRA_PICKER_TYPE", 17);
        start(context, requestCode);
    }

    public Intent pickFile(Activity context, int requestCode) {
        mPickerOptionsBundle.putInt("EXTRA_PICKER_TYPE", 18);
        return start(context, requestCode);
    }

    public void pickFile(Fragment context, int requestCode) {
        mPickerOptionsBundle.putInt("EXTRA_PICKER_TYPE", 18);
        start(context, requestCode);
    }

    private Intent start(Activity context, int requestCode) {
        if ((Build.VERSION.SDK_INT >= 23) && (ContextCompat.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") != 0)) {
            Toast.makeText(context, context.getResources().getString(Utils.getR("string.permission_filepicker_rationale")), 0).show();
            return null;
        }

        PickerManager.getInstance().setProviderAuthorities(context.getApplicationContext().getPackageName() + ".droidninja.filepicker.provider");
        Intent intent = new Intent(context, FilePickerActivity.class);
        intent.putExtras(mPickerOptionsBundle);
        return intent;
//        context.startActivityForResult(intent, requestCode);
    }

    private void start(Fragment fragment, int requestCode) {
        if ((Build.VERSION.SDK_INT >= 23) && (ContextCompat.checkSelfPermission(fragment.getContext(), "android.permission.WRITE_EXTERNAL_STORAGE") != 0)) {
            Toast.makeText(fragment.getContext(), fragment.getContext().getResources().getString(Utils.getR("string.permission_filepicker_rationale")), 0).show();
            return;
        }

        PickerManager.getInstance().setProviderAuthorities(fragment.getContext().getApplicationContext().getPackageName() + ".droidninja.filepicker.provider");
        Intent intent = new Intent(fragment.getActivity(), FilePickerActivity.class);
        intent.putExtras(mPickerOptionsBundle);
        fragment.startActivityForResult(intent, requestCode);
    }
}
