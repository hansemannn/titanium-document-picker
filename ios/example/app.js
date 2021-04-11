import DocumentPicker from 'ti.documentpicker';

const window = Ti.UI.createWindow({
    backgroundColor: '#fff'
});

const btn = Ti.UI.createButton({ title: 'Select files!' });

btn.addEventListener('click', function () {
    DocumentPicker.showDocumentPicker({
        types: [ 'com.adobe.pdf' ], // Optional, default: [ 'com.adobe.pdf', 'public.png', 'public.jpeg' ]. A list of UTIs can be found here: https://developer.apple.com/library/archive/documentation/Miscellaneous/Reference/UTIRef/Articles/System-DeclaredUniformTypeIdentifiers.html#//apple_ref/doc/uid/TP40009259-SW1
        allowsMultipleSelection: true, // Optional, default: false
        shouldShowFileExtensions: true, // Optional, default: false
        // directoryURL: '', <-- Optional, can be used to present a certain directory
        onSelect: files => { // This is only fired if not cancelled and at least one document is selected
            console.warn(files); // A string array of all selected files
        }
    })
});

window.add(btn);
window.open();
