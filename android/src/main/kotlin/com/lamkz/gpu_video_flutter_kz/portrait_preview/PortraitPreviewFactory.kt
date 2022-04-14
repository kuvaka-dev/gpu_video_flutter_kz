package com.lamkz.gpu_video_flutter_kz.portrait_preview

import android.app.Activity
import android.content.Context
import android.util.Log
import com.lamkz.gpu_video_flutter_kz.movie_preview.MoviePreviewView
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class PortraitPreviewFactory(private val activity: Activity) :
    PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    private lateinit var portraitPreviewView: PortraitPreviewView
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        Log.d("laksjdlasdj","Recreate")
        val creationParams = args as Map<String?, Any?>?
        portraitPreviewView = PortraitPreviewView(context, creationParams, activity)
        return portraitPreviewView
    }

    fun setPosition(position: Int) {
        portraitPreviewView.setFilter(position)
    }

    fun startRecordVideo() {
        portraitPreviewView.startRecordVideo()
    }

    fun stopRecordVideo(onResultPath : (String) -> Unit) {
        onResultPath(portraitPreviewView.stopRecordVideo())
    }

    fun switchCamera() {
        portraitPreviewView.switchCamera()
    }

    fun turnOnOffFlash() {
        portraitPreviewView.turnOnOffFlash()
    }

    fun captureImage() {
        portraitPreviewView.captureImage()
    }
}