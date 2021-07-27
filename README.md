# Native Document Picker in Titanium

The the native document pickers (iOS / Android) to select files from the local device, iCloud (iOS) and Google Storage (Android).

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
