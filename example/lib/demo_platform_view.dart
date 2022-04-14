import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:gpu_video_flutter_kz/camera_view_type.dart';
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
              return Container();
            }
        }
      },
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
}
