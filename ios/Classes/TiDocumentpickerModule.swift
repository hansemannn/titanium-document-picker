//
//  TiDocumentpickerModule.swift
//  titanium-document-picker
//
//  Created by Your Name
//  Copyright (c) 2019 Your Company. All rights reserved.
//

import UIKit
import TitaniumKit

/**
 
 Titanium Swift Module Requirements
 ---
 
 1. Use the @objc annotation to expose your class to Objective-C (used by the Titanium core)
 2. Use the @objc annotation to expose your method to Objective-C as well.
 3. Method arguments always have the "[Any]" type, specifying a various number of arguments.
 Unwrap them like you would do in Swift, e.g. "guard let arguments = arguments, let message = arguments.first"
 4. You can use any public Titanium API like before, e.g. TiUtils. Remember the type safety of Swift, like Int vs Int32
 and NSString vs. String.
 
 */

@objc(TiDocumentpickerModule)
class TiDocumentpickerModule: TiModule {

  public let testProperty: String = "Hello World"
  
  func moduleGUID() -> String {
    return "3f4e12c8-b5ff-46a1-a20f-1be2d8b8c5c8"
  }
  
  override func moduleId() -> String! {
    return "ti.documentpicker"
  }

  override func startup() {
    super.startup()
    debugPrint("[DEBUG] \(self) loaded")
  }

  @objc(example:)
  func example(arguments: Array<Any>?) -> String {
    // Example method. 
    // Call with "MyModule.example(args)"

    return "hello world!"
  }
  
  @objc public var exampleProp: String {
     get { 
        // Example property getter
        return "Titanium rocks!"
     }
     set {
        // Example property setter
        // Call with "MyModule.exampleProp = 'newValue'"
        self.replaceValue(newValue, forKey: "exampleProp", notification: false)
     }
   }
}
