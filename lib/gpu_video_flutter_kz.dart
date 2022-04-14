import 'dart:async';

import 'package:flutter/services.dart';

class GpuVideoFlutterKz {
  static const MethodChannel _channel = MethodChannel('gpu_video_flutter_kz');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> filterVideo(int position) async {
    final String result =
        await _channel.invokeMethod("filterVideo", {"position": position});
    return result;
  }

  static Future<String> filterCameraRecorder(int position) async {
    final String result = await _channel
        .invokeMethod("filterCameraRecorder", {"position": position});
    return result;
  }

  static Future<String> recordCameraVideo() async{
    final String result = await _channel.invokeMethod("recordCameraVideo");
    return result;
  }

  static Future<String> stopRecordCameraVideo() async{
    final String result = await _channel.invokeMethod("stopRecordCameraVideo");
    return result;
  }

  static Future<String> switchCamera() async{
    final String result = await _channel.invokeMethod("switchCamera");
    return result;
  }

  static Future<String> turnOnOffFlash() async{
    final String result = await _channel.invokeMethod("turnOnOffFlash");
    return result;
  }

  static Future<String> captureImage() async{
    final String result = await _channel.invokeMethod("captureImage");
    return result;
  }


}
