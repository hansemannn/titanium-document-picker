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

  public let testProperty: String = "Hello World"
  
  func moduleGUID() -> String {
    return "3f4e12c8-b5ff-46a1-a20f-1be2d8b8c5c8"
  }
  
  override func moduleId() -> String! {
    return "ti.documentpicker"
  }

  @objc(showDocumentPicker:)
  func showDocumentPicker(arguments: [[String: Any]]?) {

    let types = [String(kUTTypeText), String(kUTTypeContent), String(kUTTypeItem), String(kUTTypeData)]
    let picker = UIDocumentPickerViewController(documentTypes: types, in: .import)
    
    picker.delegate = self

    if #available(iOS 11.0, *) {
      picker.allowsMultipleSelection = true
    }
    
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
    fireEvent("cancel")
  }
  
  func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
    fireEvent("success", with: ["documents": urls.map { $0.absoluteString }])
  }
  
  // DEPRECATED: iOS < 10 compatibility
  func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentAt url: URL) {
    fireEvent("success", with: ["documents": [url]])
  }
}
