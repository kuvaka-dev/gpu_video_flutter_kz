import 'package:flutter/material.dart';
import 'package:gpu_video_flutter_kz/filter_type.dart';
import 'package:gpu_video_flutter_kz/gpu_video_flutter_kz.dart';

class CameraPreviewView extends StatefulWidget {
  const CameraPreviewView({Key? key, required this.cameraView})
      : super(key: key);
  final Widget cameraView;

  @override
  State<CameraPreviewView> createState() => _CameraPreviewViewState();
}

class _CameraPreviewViewState extends State<CameraPreviewView> {
  String textButtonRecordVideo = "RECORD VIDEO";

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Stack(
          children: [
            Positioned.fill(child: Align(alignment: Alignment.center, child: widget.cameraView)),
            Positioned(
              child: ListView.separated(
                itemCount: FilterType.values.length,
                separatorBuilder: (BuildContext context, int index) =>
                    const Divider(),
                itemBuilder: (BuildContext context, int index) {
                  return InkWell(
                    onTap: () => _onItemClick(index),
                    child: ListTile(
                      title: Text(FilterType.values[index].name),
                    ),
                  );
                },
              ),
            ),
            Positioned(
              left: 0,
              right: 0,
              bottom: 0,
              child: TextButton(
                onPressed: () {
                  _onButtonRecordClick();
                },
                child: Text(textButtonRecordVideo),
              ),
            ),
            Positioned(
              top: 0,
              right: 0,
              child: Column(
                children: [
                  TextButton(
                      onPressed: () {
                        _onButtonSwitchClick();
                      },
                      child: const Text("SWITCH")),
                  TextButton(
                      onPressed: () {
                        _onButtonFlashClick();
                      },
                      child: const Text("FLASH")),
                  TextButton(
                      onPressed: () {
                        _onButtonImageCaptureClick();
                      },
                      child: const Text("IMAGE CAPTURE")),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _onItemClick(int index) {
    GpuVideoFlutterKz.filterCameraRecorder(index);
  }

  void _onButtonRecordClick() {
    setState(() {
      if (textButtonRecordVideo == "RECORD VIDEO") {
        GpuVideoFlutterKz.recordCameraVideo();
        textButtonRecordVideo = "STOP";
      } else {
        GpuVideoFlutterKz.stopRecordCameraVideo();
        textButtonRecordVideo = "RECORD VIDEO";
      }
    });
  }

  void _onButtonSwitchClick() {
    GpuVideoFlutterKz.switchCamera();
  }

  void _onButtonFlashClick() {
    GpuVideoFlutterKz.turnOnOffFlash();
  }

  void _onButtonImageCaptureClick() {
    GpuVideoFlutterKz.captureImage();
  }
}
