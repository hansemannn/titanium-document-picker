
var win = Ti.UI.createWindow({backgroundColor:'white'});

var button = Ti.UI.createButton({title : 'Open File Picker'});
button.addEventListener('click', openPicker);

win.add(button);
win.open();

function openPicker(e) {
	if (Ti.Filesystem.hasStoragePermissions()) {
		showFilePicker();
		
	} else {
		Ti.Filesystem.requestStoragePermissions(function (e) {
			if (e.success) {
				showFilePicker();
		
			} else {
				alert("Permissions required...");
			}
		});
	}
}

function showFilePicker() {
	if (OS_ANDROID) {
		var ti_filepicker = require('ti.filepicker');

		ti_filepicker.pick({
			'fileType' : ti_filepicker.FILE_TYPE_MEDIA,		// or 'FILE_TYPE_DOC', {Optional} {default 'FILE_TYPE_DOC'}
			'maxCount' : 5,					// 'integer' {Optional} (Total files to select) - Must be greater than 0
			'theme' : 'MyCustomTheme',		// 'string' {Optional} {default 'LibAppTheme'} - Any non-action bar theme without `@style.` prefix
			'title' : 'All files',			// 'string' {Optional} {default 'Select Files'} - Title of the selection window
			'enableVideoPicker' : false,	// 'boolean' {Optional} {default 'true'} - Enable the selection of video files
			'enableImagePicker' : false,	// 'boolean' {Optional} {default 'true'} - Enable the selection of image files
			'enableSelectAll' : false,		// 'boolean' {Optional} {default 'false'} - Enable the selection/de-selection of all files, doesn't obey 'maxCount'
			'enableGif' : false,			// 'boolean' {Optional} {default 'false'} - Enable the selection of GIF files
			'enableFolderView' : false,		// 'boolean' {Optional} {default 'true'} - Enable the folder-view UI
			'enableDocSupport' : false,		// 'boolean' {Optional} {default 'true'} - Enable the default device document picker
			'enableCameraSupport' : false,	// 'boolean' {Optional} {default 'true'} - Enable the camera-icon to take new pictures
			'selectedFiles' : '',			// 'Array' {Optional} {default '[]'} - File paths to show already selected-files
			'cameraIcon' : 'wheel',	// 'String' {Optional} {default 'camera-icon'} - Name of the drawable icon without `.png` suffix. Placed inside platform-android-res-drawable-* folders

			'callback' : function (result) {
				if (result.success) {
					Ti.API.info('Files - ' + JSON.stringify(result.files));

				} else {
					alert(result.message);
				}
			},
		});
	}
}