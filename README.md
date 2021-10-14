# Native Document Picker in Titanium

The the native document pickers (iOS / Android) to select files from the local device, iCloud (iOS) and Google Storage (Android).

## Todo's

- [ ] Both modules may not have full parity on the file selection

## Example

```js
import TiDocumentPicker from 'ti.documentpicker';

var win = Ti.UI.createWindow({
	backgroundColor: '#fff'
});
var btn = Ti.UI.createButton({
	title: "pick"
});

btn.addEventListener("click", function(e) {

	var mimeTypes = (OS_ANDROID) ? ["application/pdf", "audio/mpeg"] : ['com.adobe.pdf', 'public.jpeg', 'public.png']

	TiDocumentPicker.showDocumentPicker({
		types: mimeTypes,
		allowMultiple: true,
		onSelect: function(result) {
			Ti.API.info('Files - ' + JSON.stringify(result.documents));
			if (OS_ANDROID && result.success === false) {
				alert(result.message);
			}
		},
	});
})

win.add(btn);
win.open();
```

## Methods
* showDocumentPicker():
 * parameter:
 <b>types</b>: String array e.g. ` ["application/pdf"]` and `'com.adobe.pdf']`
 <b>allowMultiple</b>: boolean to allow multi file selection
 <b>onSelect</b>: callback function. Returns `result.documents`
 <b>directoryURL</b> (iOS only): String with folder name
 <b>shouldShowFileExtensions</b> (iOS only): boolean to show extensions or not


## License

MIT

## Author

Hans Knöchel
