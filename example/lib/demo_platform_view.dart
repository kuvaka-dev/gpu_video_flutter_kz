import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:gpu_video_flutter_kz/camera_view_type.dart';
import 'package:gpu_video_flutter_kz/video_item.dart';
import 'package:gpu_video_flutter_kz_example/main.dart';
import 'package:gpu_video_flutter_kz/gpu_movie_preview.dart';
import 'package:gpu_video_flutter_kz/gpu_camera_record.dart';
import 'package:gpu_video_flutter_kz/filter_type.dart';
import 'package:gpu_video_flutter_kz/gpu_video_flutter_kz.dart';

import 'camera_preview_view.dart';

class DemoPlatformView extends StatefulWidget {
  const DemoPlatformView({Key? key, required this.keyFunction})
      : super(key: key);
  final KeyFunction keyFunction;

  @override
  State<DemoPlatformView> createState() => _DemoPlatformViewState();
}

class _DemoPlatformViewState extends State<DemoPlatformView> {
  bool isMute = false;
  bool isFlipHorizontal = false;
  bool isFlipVertical = false;
  List<VideoItem> videos = [];
  FilterType filterType = FilterType.DEFAULT;
  String videoSelectedPath = "";
  bool isButtonStartRecordEnable = false;

  @override
  Widget build(BuildContext context) {
    return OrientationBuilder(
      builder: (context, orientation) {
        if (orientation == Orientation.landscape) {
          SystemChrome.setPreferredOrientations(
              [DeviceOrientation.portraitDown, DeviceOrientation.portraitUp]);
        }
        switch (widget.keyFunction) {
          case KeyFunction.moviePreview:
            {
              return _buildMoviePreviewView();
            }
          case KeyFunction.cameraRecord:
            {
              return _buildCameraRecord();
            }
          case KeyFunction.mp4Compose:
            {
              return _buildMp4Compose();
            }
        }
      },
    );
  }

  Scaffold _buildMp4Compose() {
    _getVideosInGallery();
    return Scaffold(
      body: SafeArea(
        child: Column(
          children: [
            Expanded(
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  TextButton(
                    onPressed: () {
                      if (isButtonStartRecordEnable) {
                        GpuVideoFlutterKz.startCodec(isMute, isFlipHorizontal,
                            isFlipVertical, videoSelectedPath, filterType);
                      }
                    },
                    child: const Text("STARTCODEC !!"),
                  ),
                  TextButton(
                    onPressed: () {},
                    child: const Text("PLAY MOVIE !!"),
                  ),
                ],
              ),
            ),
            Expanded(
              child: SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: Row(
                  children: [
                    TextButton(
                        onPressed: () {},
                        child: const Text("FILTER: FILTER GROUP")),
                    Checkbox(
                      value: isMute,
                      onChanged: (value) {
                        isMute = value ?? false;
                      },
                    ),
                    const Text("Mute"),
                    Checkbox(
                      value: isFlipHorizontal,
                      onChanged: (value) {
                        isFlipHorizontal = value ?? false;
                      },
                    ),
                    const Text("Flip horizontal"),
                    Checkbox(
                      value: isFlipVertical,
                      onChanged: (value) {
                        setState(() {
                          isFlipVertical = value ?? false;
                        });
                      },
                    ),
                    const Text("Flip vertical"),
                  ],
                ),
              ),
            ),
            Expanded(
              child: Center(
                child:
                    TextButton(onPressed: () {}, child: const Text("Cancel")),
              ),
            ),
            Expanded(
              flex: 15,
              child: ListView.builder(
                itemBuilder: (context, index) {
                  return GestureDetector(
                    onTap: () {
                      _onItemVideoClick(index);
                    },
                    child: Container(
                      padding: const EdgeInsets.symmetric(vertical: 8),
                      color: Colors.black12,
                      height: 50,
                      child: FittedBox(
                        child: Text(
                          videos[index].path,
                        ),
                      ),
                    ),
                  );
                },
                itemCount: videos.length,
              ),
            )
          ],
        ),
      ),
    );
  }

  Scaffold _buildCameraRecord() {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            TextButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const CameraPreviewView(
                      cameraView: GPUCameraRecord(
                          cameraViewType: CameraViewType.portrait),
                    ),
                  ),
                );
              },
              child: const Text("Portrait"),
            ),
            TextButton(
                onPressed: () {
                  setState(() {
                    SystemChrome.setPreferredOrientations([
                      DeviceOrientation.landscapeLeft,
                      DeviceOrientation.landscapeRight
                    ]);
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => const CameraPreviewView(
                          cameraView: GPUCameraRecord(
                              cameraViewType: CameraViewType.landscape),
                        ),
                      ),
                    );
                  });
                },
                child: const Text("Landscape")),
            TextButton(
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => const CameraPreviewView(
                        cameraView: GPUCameraRecord(
                            cameraViewType: CameraViewType.square),
                      ),
                    ),
                  );
                },
                child: const Text("Square")),
          ],
        ),
      ),
    );
  }

  Scaffold _buildMoviePreviewView() {
    return Scaffold(
      body: Column(
        children: [
          const Expanded(
            flex: 1,
            child: GPUMoviePreview(
              videoUrl:
                  "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4",
            ),
          ),
          Expanded(
            flex: 3,
            child: ListView.separated(
              itemCount: FilterType.values.length,
              separatorBuilder: (BuildContext context, int index) =>
                  const Divider(),
              itemBuilder: (BuildContext context, int index) {
                return InkWell(
                  onTap: () => _onItemClick(FilterType.values[index]),
                  child: ListTile(
                    title: Text(FilterType.values[index].name),
                  ),
                );
              },
            ),
          )
        ],
      ),
    );
  }

  void _onItemClick(FilterType filterType) async {
    String result = await GpuVideoFlutterKz.filterVideo(filterType);
    if (result == "OK") {}
  }

  void _getVideosInGallery() async {
    List<VideoItem> list = await GpuVideoFlutterKz.getListVideo();
    setState(() {
      videos = list;
    });
  }

  void _onItemVideoClick(int index) {
    log("Item $index Clicked");
    setState(() {
      videoSelectedPath = videos[index].path;
      isButtonStartRecordEnable = true;
    });
  }
}
