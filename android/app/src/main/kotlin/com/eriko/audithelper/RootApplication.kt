package com.eriko.audithelper

import android.app.Application
import android.content.Context
import android.hardware.camera2.CameraManager
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowManager

class RootApplication : Application() {
    companion object {
        lateinit var cameraManager: CameraManager
        lateinit var screenSize: Size
        private set
    }

    override fun onCreate() {
        super.onCreate()
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val metrics = DisplayMetrics()
        display.getRealMetrics(metrics)
        screenSize = Size(metrics.widthPixels, metrics.heightPixels)
    }
}
