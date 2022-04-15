package com.lamkz.gpu_video_flutter_kz

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lamkz.gpu_video_flutter_kz.compose.Mp4ComposeManager
import com.lamkz.gpu_video_flutter_kz.movie_preview.MoviePreviewFactory
import com.lamkz.gpu_video_flutter_kz.portrait_preview.PortraitPreviewFactory

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

/** GpuVideoFlutterKzPlugin */
class GpuVideoFlutterKzPlugin : FlutterPlugin, MethodCallHandler,
    PluginRegistry.RequestPermissionsResultListener, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var binding: FlutterPlugin.FlutterPluginBinding
    private lateinit var moviePreviewFactory: MoviePreviewFactory
    private lateinit var portraitPreviewFactory: PortraitPreviewFactory
    private lateinit var mp4ComposeManager: Mp4ComposeManager
    private lateinit var activity: Activity
    private var context: Context? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        binding = flutterPluginBinding
        // init view factory
        moviePreviewFactory = MoviePreviewFactory()

        // register movie preview factory
        binding.platformViewRegistry.registerViewFactory("gpu/movie_preview", moviePreviewFactory)

        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "gpu_video_flutter_kz")
        channel.setMethodCallHandler(this)

    }

    private fun checkAppPermission() {
        requestListPermission(listPermission)
    }

    private fun checkSinglePermission(permission: String): Boolean {
        return context?.let {
            ContextCompat.checkSelfPermission(
                it,
                permission
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permission: String) {
        when (permission) {
            Manifest.permission.CAMERA -> {
                activity.let {
                    ActivityCompat.requestPermissions(
                        it, arrayOf(permission),
                        CAMERA_REQUEST_PERMISSION_CODE
                    )
                }
            }
            Manifest.permission.RECORD_AUDIO -> {
                activity.let {
                    ActivityCompat.requestPermissions(
                        it, arrayOf(permission),
                        RECORD_REQUEST_PERMISSION_CODE
                    )
                }
            }
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                activity.let {
                    ActivityCompat.requestPermissions(
                        it, arrayOf(permission),
                        STORAGE_REQUEST_PERMISSION_CODE
                    )
                }
            }
        }

    }

    private fun requestListPermission(permissions: Array<out String>) {
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION_CODE)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        try {
            when (call.method) {
                "getPlatformVersion" -> {
                    result.success("Android ${android.os.Build.VERSION.RELEASE}")
                }
                "filterVideo" -> {
                    val positionFilter: Int = call.argument<Int>("position")!!
                    Log.d("GPUVideoKzPlugin39", "$positionFilter")
                    moviePreviewFactory.setPosition(positionFilter)
                    result.success("OK")
                }
                "setFilterPercentage" ->{
                    val percentFilter : Int = call.argument<Int>("percent")!!
                    moviePreviewFactory.setFilterPercentage(percentFilter)
                    result.success("OK")
                }
                "filterCameraRecorder" -> {
                    val positionFilter: Int = call.argument<Int>("position")!!
                    portraitPreviewFactory.setPosition(positionFilter)
                    result.success("OK")
                }
                "recordCameraVideo" -> {
                    portraitPreviewFactory.startRecordVideo()
                    result.success("OK")
                }
                "stopRecordCameraVideo" -> {
                    portraitPreviewFactory.stopRecordVideo {
                        result.success(it)
                    }
                }
                "switchCamera" -> {
                    portraitPreviewFactory.switchCamera()
                    result.success("OK")
                }
                "turnOnOffFlash" -> {
                    portraitPreviewFactory.turnOnOffFlash()
                    result.success("OK")
                }
                "captureImage" -> {
                    portraitPreviewFactory.captureImage {
                        result.success(it)
                    }
                }
                "getListVideoInGallery" -> {
                    mp4ComposeManager.getListVideoInGallery {
                        result.success(it)
                    }
                }
                "startCodec" -> {
                    val isMute: Boolean = call.argument<Boolean>("isMute")!!
                    val isFlipHorizontal: Boolean = call.argument<Boolean>("isFlipHorizontal")!!
                    val isFlipVertical: Boolean = call.argument<Boolean>("isFlipVertical")!!
                    val videoSelectedPath: String = call.argument<String>("videoSelectedPath")!!
                    val positionFilter: Int = call.argument<Int>("filterPosition")!!
                    mp4ComposeManager.startCodec(
                        isMute,
                        isFlipHorizontal,
                        isFlipVertical,
                        videoSelectedPath,
                        positionFilter
                    )
                    result.success("OK")
                }
                else -> {
                    result.notImplemented()
                }
            }
        } catch (ex: Exception) {
            Toast.makeText(activity, "$ex", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>?,
        grantResults: IntArray?
    ): Boolean {
        when (requestCode) {
            CAMERA_REQUEST_PERMISSION_CODE -> {
                permissions?.let {
                    if (grantResults?.get(0) ?: 1 == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(activity, "Camera Permission Granted", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            STORAGE_REQUEST_PERMISSION_CODE -> {
                permissions?.let {
                    if (grantResults?.get(0) ?: 1 == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(activity, "Storage Permission Granted", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            RECORD_REQUEST_PERMISSION_CODE -> {
                permissions?.let {
                    if (grantResults?.get(0) ?: 1 == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(activity, "Record Permission Granted", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            REQUEST_PERMISSION_CODE -> {
                permissions?.let {
                    it.forEachIndexed { index, value ->
                        if (grantResults?.get(index) ?: 1 == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(activity, "$value Granted", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        return true
    }

    override fun onAttachedToActivity(activityBinding: ActivityPluginBinding) {
        activity = activityBinding.activity
        activity.let {
            context = it.applicationContext
            mp4ComposeManager = Mp4ComposeManager(
                it.applicationContext
            )
        }
        checkAppPermission()
        portraitPreviewFactory = PortraitPreviewFactory(activity)
        binding.platformViewRegistry.registerViewFactory(
            "gpu/camera_record_portrait",
            portraitPreviewFactory
        )

    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
        activity.let {
            context = it.applicationContext
        }
    }

    override fun onDetachedFromActivity() {
    }


    companion object {
        val listPermission = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        const val CAMERA_REQUEST_PERMISSION_CODE = 12391
        const val RECORD_REQUEST_PERMISSION_CODE = 12392
        const val STORAGE_REQUEST_PERMISSION_CODE = 12393
        const val REQUEST_PERMISSION_CODE = 12394
    }
}
