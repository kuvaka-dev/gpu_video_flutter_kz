import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:flutter/material.dart';
import 'package:gpu_video_flutter_kz/camera_view_type.dart';

class GPUCameraRecord extends StatelessWidget {
  const GPUCameraRecord({Key? key, required this.cameraViewType})
      : super(key: key);
  final CameraViewType cameraViewType;

  @override
  Widget build(BuildContext context) {
    // This is used in the platform side to register the view.
    const String viewType = 'gpu/camera_record_portrait';
    // Pass parameters to the platform side.
    int videoWidth = 720;
    int videoHeight = 720;
    int cameraWidth = 1280;
    int cameraHeight = 720;
    switch (cameraViewType) {
      case CameraViewType.portrait:
        {
          videoWidth = 720;
          videoHeight = 1280;
          cameraWidth = 1280;
          cameraHeight = 720;
          break;
        }
      case CameraViewType.landscape:
        {
          videoWidth = 1280;
          videoHeight = 720;
          cameraWidth = 1280;
          cameraHeight = 720;
          break;
        }
      default: break;
    }
    Map<String, dynamic> creationParams = <String, dynamic>{
      "videoWidth": videoWidth,
      "videoHeight": videoHeight,
      "cameraWidth": cameraWidth,
      "cameraHeight": cameraHeight
    };

    return PlatformViewLink(
      viewType: viewType,
      surfaceFactory:
          (BuildContext context, PlatformViewController controller) {
        return AndroidViewSurface(
          controller: controller as AndroidViewController,
          gestureRecognizers: const <Factory<OneSequenceGestureRecognizer>>{},
          hitTestBehavior: PlatformViewHitTestBehavior.opaque,
        );
      },
      onCreatePlatformView: (PlatformViewCreationParams params) {
        return PlatformViewsService.initSurfaceAndroidView(
          id: params.id,
          viewType: viewType,
          layoutDirection: TextDirection.ltr,
          creationParams: creationParams,
          creationParamsCodec: const StandardMessageCodec(),
          onFocus: () {
            params.onFocusChanged(true);
          },
        )
          ..addOnPlatformViewCreatedListener(params.onPlatformViewCreated)
          ..create();
      },
    );
  }
}
