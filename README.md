# Native Document Picker in Titanium

The the native document pickers (iOS / Android) to select files from the local device, iCloud (iOS) and Google Storage (Android).

## Todo's

- [ ] Currently, the Android module import is `ti.filepicker`, on iOS it's `ti.documentpicker`
- [ ] Both modules may not have full parity on the file selection
- [ ] Add cross-platform example (currently the example/app.js is Android-only) 

## Example

```js
  import TiDocumentPicker from 'ti.documentpicker';

  TiDocumentPicker.showDocumentPicker({
    types: [ 'com.adobe.pdf', 'public.jpeg', 'public.png' ],
    allowsMultipleSelection: false,
    onSelect: event => {
      console.warn(event.documents);
    }
  });
```

## License

MIT

## Author

Hans Knöchel
