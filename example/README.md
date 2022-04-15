# GPU Video Flutter
- GPU Video Flutter in Android
- Base on source GPU Video Flutter : https://github.com/MasayukiSuda/GPUVideo-android
- We create a Plugin for Flutter Developer who want to apply filter in their own images, videos but can not find any library for it

## About

* What is it, what does it do
    -  Customize filter in your own video
    -  Record videos, take images with many filters
    -  Work with flash, camera back of front, ...
    -  Support landscape or portrait or square camera type

* Project status: working/prototype
  Done 3/3 Function
  Next Clean and Re-Arrange Code

## Table of contents

Use for instance <https://github.com/ekalinin/github-markdown-toc>:

>   * [About](#about)
>   * [Table of contents](#table-of-contents)
>   * [Installation](#installation)
>   * [Usage](#usage)
>   * [Features](#features)
>   * [Content](#content)
>   * [Requirements](#requirements)
>   * [Resources (Documentation and other links)](#resources-documentation-and-other-links)
>   * [License](#license)

## Installation
- Add this library into your pubspec.yaml file:
  ```dart
    gpu_video_flutter_kz: ^0.0.4
  ```
- Open Project Android in new screen
    - Change your minSDKVersion to 21
      ```dart
      defaultConfig {
          minSdkVersion 21
      }
      ```
    - Add to your build.gradle (Project:android)
      ```dart
      maven { 
        url 'https://jitpack.io' 
      }
      ```
## Usage
- Movie Preview
  ```dart
  GPUMoviePreview(
    videoUrl:
        "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4",
  )
  ```
    - To use Filter for this video you can use:
  ```dart
  GpuVideoFlutterKz.filterVideo(FilterType.BILATERAL_BLUR);
  ```

    - Set Filter's Percentage:
   ```dart
   GpuVideoFlutterKz.setFilterPercentage(int percent);
   ```
- Camera Recorder
    - Init
  ```dart
  GPUCameraRecord(
        cameraViewType: CameraViewType.square),
  )
  ```
    * You can change type of camera to portrait or landscape otherwise.
    - To add filter for your preview camera, just use:
  ```dart
  GpuVideoFlutterKz.filterCameraRecorder(filterType);
  ```
    - To start record:
  ```dart
  GpuVideoFlutterKz.recordCameraVideo();
  ```
    - To end record:
  ```dart
  String videoPath = GpuVideoFlutterKz.stopRecordCameraVideo();
  // Return of stop record video is video's path
  ```
    - To switch between front and back camera:
  ```dart
  GpuVideoFlutterKz.switchCamera();
  ```
    - To turn on or off flash:
  ```dart
  GpuVideoFlutterKz.turnOnOffFlash();
  ```
    - To capture an image:
  ```dart
  String imagePath = GpuVideoFlutterKz.captureImage();
  //Return of this function is image's path
  ```
- Mp4 Compose
    - Get All Video In Gallery:
      ```dart
      List<VideoItem> videos = await GpuVideoFlutterKz.getListVideo();
      // return list of VideoItem (which is in my lib)
      ```
    - Start CodeC:
      ```dart
      GpuVideoFlutterKz.startCodec(isMute, isFlipHorizontal,
                  isFlipVertical, videoSelectedPath, filterType);
      // Apply 5 properties to your video and save the new video into your gallery
      ```
### Screenshots

### Features

### Content

Description, sub-modules organization...

### Requirements

- Require Android Min SDK 21

## Resources (Documentation and other links)
- https://github.com/MasayukiSuda/GPUVideo-android
- https://developer.android.com/guide/topics/media/exoplayer
- https://developer.android.com/kotlin/coroutines
- https://github.com/google/gson

## License

[Apache License, Version 2.0](https://github.com/lamdev99/gpu_video_flutter_kz/blob/master/LICENSE)