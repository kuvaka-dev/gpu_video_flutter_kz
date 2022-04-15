package com.lamkz.gpu_video_flutter_kz.movie_preview

import android.content.Context
import android.util.Log
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class MoviePreviewFactory : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    private lateinit var moviePreviewView: MoviePreviewView
    private var mPosition : Int = 0
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val creationParams = args as Map<String?,Any?>?
        Log.d("MoviePreviewFactory","$mPosition")
        moviePreviewView = MoviePreviewView(context,creationParams,mPosition)
        return moviePreviewView
    }
    fun setPosition(position: Int){
        moviePreviewView.setFilter(position)
    }

    fun setFilterPercentage(percentFilter: Int) {
        moviePreviewView.setFilterPercentage(percentFilter)
    }
}