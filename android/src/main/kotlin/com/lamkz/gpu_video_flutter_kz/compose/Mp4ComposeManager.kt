package com.lamkz.gpu_video_flutter_kz.compose

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.daasuu.gpuv.composer.FillMode
import com.daasuu.gpuv.composer.GPUMp4Composer
import com.daasuu.gpuv.egl.filter.GlFilter
import com.daasuu.gpuv.egl.filter.GlFilterGroup
import com.daasuu.gpuv.egl.filter.GlMonochromeFilter
import com.daasuu.gpuv.egl.filter.GlVignetteFilter
import com.google.gson.Gson
import com.lamkz.gpu_video_flutter_kz.FilterType
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Mp4ComposeManager(
    private val context: Context,
) {
    private var videoLoader: VideoLoader? = null
    private var videoItem: VideoItem? = null
    private var gpuMp4Composer: GPUMp4Composer? = null
    private var glFilter: GlFilter? = GlFilterGroup(GlMonochromeFilter(), GlVignetteFilter())
    private var videoPath: String? = null

    fun getListVideoInGallery(onResult: (List<Any>) -> Unit) {
        Log.d("Mp4Loader", "asdasd")
        val videos = arrayListOf<Any>()
        val gson = Gson()
        videoLoader = VideoLoader(context)
        videoLoader?.loadDeviceVideos(object : VideoLoadListener {
            override fun onVideoLoaded(items: List<VideoItem>) {
                items.forEach {
                    videos.add(gson.toJson(it))
                }
                onResult(videos)
            }

            override fun onFailed(e: java.lang.Exception) {
                e.printStackTrace()
            }
        })
    }

    fun startCodec(
        isMute: Boolean,
        isFlipHorizontal: Boolean,
        isFlipVertical: Boolean,
        videoSelectedPath: String,
        filterPosition: Int
    ) {
        videoPath = getVideoFilePath()
        gpuMp4Composer = null
        gpuMp4Composer =
            GPUMp4Composer(videoSelectedPath, videoPath) // .rotation(Rotation.ROTATION_270)
                //.size(720, 720)
                .fillMode(FillMode.PRESERVE_ASPECT_CROP)
                .filter(FilterType.createGlFilter(FilterType.values()[filterPosition], context))
                .mute(isMute)
                .flipHorizontal(isFlipHorizontal)
                .flipVertical(isFlipVertical)
                .listener(object : GPUMp4Composer.Listener {
                    override fun onProgress(progress: Double) {
                        Log.d("Mp4Compose", "$progress")
//                        runOnUiThread(Runnable { progressBar.progress = (progress * 100).toInt() })
                    }

                    override fun onCompleted() {
                        exportMp4ToGallery(context, videoPath!!)
                        Log.d("Mp4Compose", "DONE")
//                        runOnUiThread(Runnable {
//                            progressBar.progress = 100
//                            findViewById<View>(R.id.start_codec_button).setEnabled(true)
//                            findViewById<View>(R.id.start_play_movie).setEnabled(true)
//                            Toast.makeText(
//                                this@Mp4ComposeActivity,
//                                "codec complete path =$videoPath",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        })
                    }

                    override fun onCanceled() {}
                    override fun onFailed(exception: Exception) {
                    }
                })
                .start()
    }


    private fun getAndroidMoviesFolder(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
    }

    private fun getVideoFilePath(): String {
        return getAndroidMoviesFolder().absolutePath + "/" + SimpleDateFormat(
            "yyyyMM_dd-HHmmss",
            Locale.US
        ).format(
            Date()
        ) + "filter_apply.mp4"
    }

    fun exportMp4ToGallery(context: Context, filePath: String) {
        val values = ContentValues(2)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(MediaStore.Video.Media.DATA, filePath)
        context.contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            values
        )
        context.sendBroadcast(
            Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://$filePath")
            )
        )
    }

}