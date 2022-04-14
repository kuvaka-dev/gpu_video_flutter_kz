package com.lamkz.gpu_video_flutter_kz.portrait_preview

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.opengl.GLException
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import com.daasuu.gpuv.camerarecorder.CameraRecordListener
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorder
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorderBuilder
import com.daasuu.gpuv.camerarecorder.LensFacing
import com.lamkz.gpu_video_flutter_kz.FilterType
import com.lamkz.gpu_video_flutter_kz.widget.PortraitFrameLayout
import com.lamkz.gpu_video_flutter_kz.widget.SampleCameraGLView
import io.flutter.plugin.platform.PlatformView
import kotlinx.coroutines.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.IntBuffer
import java.text.SimpleDateFormat
import java.util.*
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.opengles.GL10

class PortraitPreviewView(
    private val context: Context,
    creationParams: Map<String?, Any?>?,
    private val activity: Activity
) : PlatformView {
    private var sampleGLView: SampleCameraGLView? = SampleCameraGLView(context)
    private var gpuCameraRecorder: GPUCameraRecorder? = null
    private var portraitFrameLayout: PortraitFrameLayout = PortraitFrameLayout(context)
    private var filepath: String? = null
    private var toggleClick = false
    private var lensFacing = LensFacing.BACK
    private var videoWidth = 0
    private var videoHeight = 0
    private var cameraWidth = 0
    private var cameraHeight = 0
    private var filePath: String = ""


    override fun getView(): View {
        return portraitFrameLayout
    }

    init {
        creationParams?.let { params ->
            videoWidth = params["videoWidth"] as Int
            videoHeight = params["videoHeight"] as Int
            cameraWidth = params["cameraWidth"] as Int
            cameraHeight = params["cameraHeight"] as Int
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            setUpCamera()
        }
    }

    private fun setUpCamera() {
        CoroutineScope(Dispatchers.Main).launch {
            portraitFrameLayout.removeAllViews()
            sampleGLView = null
            sampleGLView = SampleCameraGLView(context)
            sampleGLView!!.layoutParams = LinearLayout.LayoutParams(videoWidth,videoHeight)
            sampleGLView!!.setTouchListener { event, width, height ->
                if (gpuCameraRecorder == null) return@setTouchListener
                gpuCameraRecorder!!.changeManualFocusPoint(event.x, event.y, width, height)
            }

            withContext(Dispatchers.Main) {
                portraitFrameLayout.addView(sampleGLView)
                setUpGPUCameraRecorder()
            }
        }
    }

    // init gpu camera recorder
    private fun setUpGPUCameraRecorder() {
        gpuCameraRecorder = GPUCameraRecorderBuilder(activity, sampleGLView) //.recordNoFilter(true)
            .cameraRecordListener(object : CameraRecordListener {
                override fun onGetFlashSupport(flashSupport: Boolean) {

                }

                override fun onRecordComplete() {
                    filepath?.let { exportMp4ToGallery(context, it) }
                }

                override fun onRecordStart() {
                }

                override fun onError(exception: Exception) {
                    Log.e("GPUCameraRecorder", exception.toString())
                }

                override fun onCameraThreadFinish() {
                    if (toggleClick) {
                        CoroutineScope(Dispatchers.Main).launch { setUpCamera() }
                    }
                    toggleClick = false
                }

                override fun onVideoFileReady() {}
            })
            .videoSize(videoWidth, videoHeight)
            .cameraSize(cameraWidth, cameraHeight)
            .lensFacing(lensFacing)
            .build()
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

    override fun dispose() {
        releaseCamera()
    }

    // Apply Filter
    fun setFilter(position: Int) {
        val filterTypes = FilterType.createFilterList()
        gpuCameraRecorder?.setFilter(FilterType.createGlFilter(filterTypes[position], context))
    }

    // Start Record Video
    fun startRecordVideo() {
        filepath = getVideoFilePath()
        gpuCameraRecorder?.start(filepath)

    }

    // Stop Record Video
    fun stopRecordVideo(): String {
        gpuCameraRecorder?.stop()
        return filePath
    }

    fun switchCamera() {
        releaseCamera()
        lensFacing = if (lensFacing == LensFacing.BACK) {
            LensFacing.FRONT
        } else {
            LensFacing.BACK
        }
        toggleClick = true
    }

    private fun releaseCamera() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                sampleGLView?.onPause()
                gpuCameraRecorder?.let {
                    it.stop()
                    it.release()
                }
                gpuCameraRecorder = null
                sampleGLView?.let {
                    portraitFrameLayout.removeView(sampleGLView)
                }
                sampleGLView = null
                setUpCamera()
            } catch (ex: Exception) {
                Toast.makeText(activity, "$ex", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getVideoFilePath(): String =
        getAndroidMoviesFolder().absolutePath + "/" + SimpleDateFormat(
            "yyyyMM_dd-HHmmss", Locale.US
        ).format(
            Date()
        ) + "GPUCameraRecorder.mp4"


    private fun getAndroidMoviesFolder(): File {
        return Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MOVIES
        )
    }

    private fun exportPngToGallery(context: Context, filePath: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(filePath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }

    fun getImageFilePath(): String {
        return getAndroidImageFolder().absolutePath + "/" + SimpleDateFormat(
            "yyyyMM_dd-HHmmss", Locale.US
        ).format(
            Date()
        ) + "GPUCameraRecorder.png"
    }

    private fun getAndroidImageFolder(): File {
        return Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
    }

    fun turnOnOffFlash() {
        gpuCameraRecorder?.let {
            if (it.isFlashSupport) {
                it.switchFlashMode()
                it.changeAutoFocus()
            }
        }
    }

    fun captureImage() : String {
        var imagePath : String? = ""
        try {
            captureBitmap(object : BitmapReadyCallbacks {
                override fun onBitmapReady(bitmap: Bitmap?) {
                    CoroutineScope(Dispatchers.IO).launch {
                        imagePath =
                            getImageFilePath()
                        saveAsPngImage(bitmap!!, imagePath)
                        imagePath?.let {
                            exportPngToGallery(
                                context,
                                it
                            )
                        }
                    }
                }
            })
        } catch (ex: Exception) {
            Toast.makeText(activity, "$ex", Toast.LENGTH_SHORT).show()
        }
        return imagePath!!
    }

    private interface BitmapReadyCallbacks {
        fun onBitmapReady(bitmap: Bitmap?)
    }

    private fun captureBitmap(bitmapReadyCallbacks: BitmapReadyCallbacks) {
        sampleGLView!!.queueEvent {
            val egl = EGLContext.getEGL() as EGL10
            val gl = egl.eglGetCurrentContext().gl as GL10
            val snapshotBitmap: Bitmap = createBitmapFromGLSurface(
                sampleGLView!!.measuredWidth,
                sampleGLView!!.measuredHeight,
                gl
            )!!

            CoroutineScope(Dispatchers.Main).launch {
                bitmapReadyCallbacks.onBitmapReady(snapshotBitmap)
            }
        }
    }

    private fun createBitmapFromGLSurface(w: Int, h: Int, gl: GL10): Bitmap? {
        val bitmapBuffer = IntArray(w * h)
        val bitmapSource = IntArray(w * h)
        val intBuffer = IntBuffer.wrap(bitmapBuffer)
        intBuffer.position(0)
        try {
            gl.glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer)
            var offset1: Int
            var offset2: Int
            var texturePixel: Int
            var blue: Int
            var red: Int
            var pixel: Int
            for (i in 0 until h) {
                offset1 = i * w
                offset2 = (h - i - 1) * w
                for (j in 0 until w) {
                    texturePixel = bitmapBuffer[offset1 + j]
                    blue = texturePixel shr 16 and 0xff
                    red = texturePixel shl 16 and 0x00ff0000
                    pixel = texturePixel and -0xff0100 or red or blue
                    bitmapSource[offset2 + j] = pixel
                }
            }
        } catch (e: GLException) {
            Log.e("CreateBitmap", "createBitmapFromGLSurface: " + e.message, e)
            return null
        }
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888)
    }

    fun saveAsPngImage(bitmap: Bitmap, filePath: String?) {
        try {
            val file = filePath?.let { File(it) }
            val outStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}