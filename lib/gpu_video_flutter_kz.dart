import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:gpu_video_flutter_kz/filter_type.dart';
import 'package:gpu_video_flutter_kz/video_item.dart';

class GpuVideoFlutterKz {
  static const MethodChannel _channel = MethodChannel('gpu_video_flutter_kz');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> filterVideo(FilterType filterType) async {
    int position = FilterType.values.indexOf(filterType);
    final String result =
        await _channel.invokeMethod("filterVideo", {"position": position});
    return result;
  }

  static Future<String> setFilterPercentage(int percent) async {
    String result = await _channel
        .invokeMethod("setFilterPercentage", {"percent": percent});
    return result;
  }

  static Future<String> filterCameraRecorder(FilterType filterType) async {
    int position = FilterType.values.indexOf(filterType);
    final String result = await _channel
        .invokeMethod("filterCameraRecorder", {"position": position});
    return result;
  }

  static Future<String> recordCameraVideo() async {
    final String result = await _channel.invokeMethod("recordCameraVideo");
    return result;
  }

  static Future<String> stopRecordCameraVideo() async {
    final String result = await _channel.invokeMethod("stopRecordCameraVideo");
    return result;
  }

  static Future<String> switchCamera() async {
    final String result = await _channel.invokeMethod("switchCamera");
    return result;
  }

  static Future<String> turnOnOffFlash() async {
    final String result = await _channel.invokeMethod("turnOnOffFlash");
    return result;
  }

  static Future<String> captureImage() async {
    final String result = await _channel.invokeMethod("captureImage");
    return result;
  }

  static Future<List<VideoItem>> getListVideo() async {
    List<VideoItem> videos = [];
    final List<dynamic> result =
        await _channel.invokeMethod("getListVideoInGallery");
    final jsonDecode = json.decode(result.toString());
    for (int index = 0; index < result.length; index++) {
      videos.add(VideoItem.fromJson(jsonDecode[index]));
    }
    return videos;
  }

  static Future<String> startCodec(
      bool isMute,
      bool isFlipHorizontal,
      bool isFlipVertical,
      String videoSelectedPath,
      FilterType filterSelected) async {
    String result = await _channel.invokeMethod("startCodec", {
      "isMute": isMute,
      "isFlipHorizontal": isFlipHorizontal,
      "isFlipVertical": isFlipVertical,
      "videoSelectedPath": videoSelectedPath,
      "filterPosition": FilterType.values.indexOf(filterSelected)
    });
    return result;
  }
}
