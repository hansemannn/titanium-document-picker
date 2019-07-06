
var win = Ti.UI.createWindow({backgroundColor:'white'});

var button = Ti.UI.createButton({title : 'Open File Picker'});
button.addEventListener('click', showFilePicker);

win.add(button);
win.open();



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
			'enableSelectAll' : false,		// 'boolean' {Optional} {default 'true'} - Enable the selection/de-selection of all files, doesn't obey 'maxCount'
			'enableGif' : false,			// 'boolean' {Optional} {default 'false'} - Enable the selection of GIF files
			'enableFolderView' : false,		// 'boolean' {Optional} {default 'true'} - Enable the folder-view UI
			'enableDocSupport' : false,		// 'boolean' {Optional} {default 'true'} - Enable the selection of documents type files
			'enableCameraSupport' : false,	// 'boolean' {Optional} {default 'true'} - Enable the camera-icon to take new pictures
			'selectedFiles' : '',			// Not supported yet
			'cameraIcon' : '',				// Not supported yet
			'orientation' : '',				// Not supported yet, but follows the app's orientation

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
