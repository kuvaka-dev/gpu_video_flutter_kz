import Flutter
import UIKit

public class SwiftGpuVideoFlutterKzPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "gpu_video_flutter_kz", binaryMessenger: registrar.messenger())
    let instance = SwiftGpuVideoFlutterKzPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    result("iOS " + UIDevice.current.systemVersion)
  }
}
