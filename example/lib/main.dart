// ignore_for_file: prefer_const_constructors

import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:gpu_video_flutter_kz_example/demo_platform_view.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: HomeView(),
      theme: ThemeData(
        highlightColor: Colors.blueAccent,
      ),
    );
  }

  void _onMp4ComposeButtonClick(BuildContext context) {}

  void _onMoviePreviewButtonClick(BuildContext context) {
    log("123123");
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => DemoPlatformView(
          keyFunction: KeyFunction.moviePreview,
        ),
      ),
    );
  }

  void _onCameraRecordButtonClick(BuildContext context) {}
}

class HomeView extends StatelessWidget {
  const HomeView({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            TextButton(
                onPressed: () => _onMp4ComposeButtonClick(context),
                child: Text("Mp4 Compose")),
            TextButton(
                onPressed: () => _onCameraRecordButtonClick(context),
                child: Text("Camera Record")),
            TextButton(
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => DemoPlatformView(
                          keyFunction: KeyFunction.moviePreview),
                    ),
                  );
                },
                child: Text("Movie Preview")),
          ],
        ),
      ),
    );
  }

  _onMp4ComposeButtonClick(BuildContext context) {}

  void _onCameraRecordButtonClick(BuildContext context) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) =>
            DemoPlatformView(keyFunction: KeyFunction.cameraRecord),
      ),
    );
  }
}

enum KeyFunction { mp4Compose, cameraRecord, moviePreview }
