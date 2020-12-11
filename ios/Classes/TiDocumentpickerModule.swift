//
//  TiDocumentpickerModule.swift
//  titanium-document-picker
//
//  Created by Your Name
//  Copyright (c) 2019 Your Company. All rights reserved.
//

import MobileCoreServices
import TitaniumKit
import UIKit

@objc(TiDocumentpickerModule)
class TiDocumentpickerModule: TiModule {

  func moduleGUID() -> String {
    return "3f4e12c8-b5ff-46a1-a20f-1be2d8b8c5c8"
  }

  override func moduleId() -> String! {
    return "ti.documentpicker"
  }

  private var onSelectCallback: KrollCallback?

  @objc(showDocumentPicker:)
  func showDocumentPicker(arguments: [[String: Any]]?) {
    let types = [String(kUTTypePDF), String(kUTTypePNG), String(kUTTypeJPEG)]
    let picker = UIDocumentPickerViewController(documentTypes: types, in: .import)

    if let arguments = arguments, arguments.first != nil {
      onSelectCallback = arguments.first?["onSelect"] as? KrollCallback
    }

    picker.delegate = self

    guard let controller = TiApp.controller(), let topPresentedController = controller.topPresentedController() else {
      print("[WARN] No window opened. Ignoring gallery call …")
      return
    }

    topPresentedController.present(picker, animated: true, completion: nil)
  }
}

// MARK: UIDocumentPickerDelegate

extension TiDocumentpickerModule: UIDocumentPickerDelegate {

  func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
    // No-OP
  }

  func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
    onSelectCallback?.call([["documents": urls.map { $0.absoluteString } ]], thisObject: self)
  }

  // DEPRECATED: iOS < 11 compatibility
  func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentAt url: URL) {
    onSelectCallback?.call([["documents": [url.absoluteString] ]], thisObject: self)
  }
}
