package com.lamkz.gpu_video_flutter_kz.movie_preview

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.daasuu.gpuv.egl.filter.GlFilter
import com.daasuu.gpuv.player.GPUPlayerView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.lamkz.gpu_video_flutter_kz.FilterAdjuster
import com.lamkz.gpu_video_flutter_kz.FilterType
import com.lamkz.gpu_video_flutter_kz.widget.MovieWrapperView
import io.flutter.plugin.platform.PlatformView

internal class MoviePreviewView(
    private val context: Context,
    creationParams: Map<String?, Any?>?,
    position: Int
) : PlatformView {
    private var player: SimpleExoPlayer? = null
    private var gpuPlayerView: GPUPlayerView = GPUPlayerView(context)
    private val movieWrapperView = MovieWrapperView(context)
    private var filter: GlFilter? = null
    private var adjuster: FilterAdjuster? = null
    override fun getView(): View {
        return movieWrapperView
    }

    override fun dispose() {
        gpuPlayerView.onPause()
        movieWrapperView.removeAllViews()
        player!!.stop()
        player!!.release()
        player = null
    }

    init {
        Log.d("MoviePreviewView","$position")
        creationParams?.let { params ->
            val videoUrl: String = params["videoUrl"] as String
            // SimpleExoPlayer
            player = SimpleExoPlayer.Builder(context)
                .setTrackSelector(DefaultTrackSelector(context))
                .build()

            player!!.addMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
            player!!.prepare()
            player!!.playWhenReady = true
            gpuPlayerView.setSimpleExoPlayer(player)
            gpuPlayerView.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val filterTypes = FilterType.createFilterList()
            filter = FilterType.createGlFilter(filterTypes[position + 1], context)
            adjuster = FilterType.createFilterAdjuster(filterTypes[position + 1])
            adjuster?.adjust(filter, 50)
            gpuPlayerView.setGlFilter(filter)
            movieWrapperView.setBackgroundColor(Color.CYAN)
            movieWrapperView.addView(gpuPlayerView)
            gpuPlayerView.onResume()
        }
    }
    fun setFilter(position: Int){
        val filterTypes = FilterType.createFilterList()
        filter = FilterType.createGlFilter(filterTypes[position], context)
        adjuster = FilterType.createFilterAdjuster(filterTypes[position])
        adjuster?.adjust(filter, 50)
        gpuPlayerView.setGlFilter(filter)
    }
}