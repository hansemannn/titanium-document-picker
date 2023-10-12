//
//  TiDocumentpickerModule.swift
//  titanium-document-picker
//
//  Created by Hans Knöchel
//  Copyright (c) 2019-present Hans Knöchel. No rights reserved.
//

import MobileCoreServices
import TitaniumKit
import UIKit
import UniformTypeIdentifiers

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
    guard let arguments = arguments, let params = arguments.first else { return }

    onSelectCallback = params["onSelect"] as? KrollCallback

    let types = mappedTypes(from: params["types"] as? [String])
    let allowsMultipleSelection = params["allowMultiple"] as? Bool ?? false
    let shouldShowFileExtensions = params["shouldShowFileExtensions"] as? Bool ?? false
    let directoryURL = TiUtils.stringValue(params["directoryURL"])
    var picker: UIDocumentPickerViewController!

    // Use new API on iOS 14+
    if #available(iOS 14.0, *) {
      picker = UIDocumentPickerViewController(forOpeningContentTypes: types.map({ UTType($0)! }), asCopy: true)
    } else {
      picker = UIDocumentPickerViewController(documentTypes: types, in: .import)
    }

    picker.delegate = self
    picker.allowsMultipleSelection = allowsMultipleSelection

    if #available(iOS 13.0, *) {
      picker.shouldShowFileExtensions = shouldShowFileExtensions
      if let directoryURL = directoryURL {
        picker.directoryURL = URL(string: directoryURL)
      }
    }

    guard let controller = TiApp.controller(), let topPresentedController = controller.topPresentedController() else {
      print("[WARN] No window opened. Ignoring gallery call …")
      return
    }

    topPresentedController.present(picker, animated: true, completion: nil)
  }

  private func mappedTypes(from proxyValues: [String]?) -> [String] {
    // Default: Only PDF, PNG and JPEG for backwards compatibility
    guard let proxyValues = proxyValues else {
      return [String(kUTTypePDF), String(kUTTypePNG), String(kUTTypeJPEG)]
    }

    return proxyValues
  }

  @objc(export:)
  func export(arguments: [[String: Any]]?) {

    guard let arguments = arguments, let params = arguments.first else { return }

    onSelectCallback = params["onSelect"] as? KrollCallback

    let urls = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
    let docsDirectory = urls[0]

    guard let file = TiUtils.stringValue(params["file"]) else { return }

    let url = docsDirectory.appendingPathComponent(file)

    let filePath = url.path

    if !FileManager.default.fileExists(atPath: filePath) {
      print("[WARN] export could not find file at path: \(filePath)")
      return
    }

    var picker: UIDocumentPickerViewController!

    // Use new API on iOS 14+
    if #available(iOS 14.0, *) {
      picker = UIDocumentPickerViewController(forExporting: [url], asCopy: true)
    } else {
      picker = UIDocumentPickerViewController(urls: [url], in: .exportToService)
    }

    picker.delegate = self

    let directoryURL = TiUtils.stringValue(params["directoryURL"])

    if #available(iOS 13.0, *) {
      if let directoryURL = directoryURL {
        picker.directoryURL = URL(string: directoryURL)
      }
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
    // NO-OP for now
  }

  func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
    onSelectCallback?.call([["documents": urls.map { $0.absoluteString } ]], thisObject: self)
  }

  // DEPRECATED: iOS < 11 compatibility
  func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentAt url: URL) {
    onSelectCallback?.call([["documents": [url.absoluteString] ]], thisObject: self)
  }
}
