package com.eriko.audithelper

import android.annotation.SuppressLint
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
import android.hardware.camera2.CameraCharacteristics.SENSOR_ORIENTATION
import android.hardware.camera2.CameraDevice

import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.util.Size
import android.view.Surface
import io.reactivex.Observable
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class CameraCore {
    var cameraDevice: CameraDevice? = null
    var captureSession: CameraCaptureSession? = null
    var previewSize: Size = Size(0, 0)

    private var requestBuilder: CaptureRequest.Builder? = null
    private var sensorOrientation: Int = 0
    private val lock = Semaphore(1)

    init {
        val manager = RootApplication.cameraManager
        val cameraId = manager.cameraIdList[0] ?: ""
        val characteristics = manager.getCameraCharacteristics(cameraId)
        val map = characteristics.get(SCALER_STREAM_CONFIGURATION_MAP)
            ?: throw RuntimeException("No Configuration")

        previewSize = chooseSizeFitWithScreen(map.getOutputSizes(SurfaceTexture::class.java))
        sensorOrientation = characteristics.get(SENSOR_ORIENTATION)!!
    }

    fun startPreview(surface: List<Surface>): Observable<Boolean> {
        return Observable.create {
            if (!lock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                it.onNext(false)
                it.onComplete()
                return@create
            }

            if (cameraDevice == null || surface.isEmpty()) {
                it.onNext(false)
                it.onComplete()
                lock.release()
            } else {
                requestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                requestBuilder?.addTarget(surface[0])
                cameraDevice?.createCaptureSession(surface, object : CameraCaptureSession.StateCallback() {
                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        lock.release()
                        it.onNext(false)
                        it.onComplete()
                    }

                    override fun onConfigured(session: CameraCaptureSession) {
                        this@CameraCore.captureSession = session
                        lock.release()
                        previewNow()
                        it.onNext(true)
                        it.onComplete()
                    }
                }, null)
            }
        }
    }

    fun close() {
        try {
            lock.acquire()
            captureSession?.close()
            captureSession = null
            cameraDevice?.close()
            cameraDevice = null
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            lock.release()
        }
    }

    @SuppressLint("MissingPermission")
    fun openCamera(): Observable<Boolean> {
        return Observable.create {
            val manager = RootApplication.cameraManager
            manager.openCamera(
                manager.cameraIdList[0] ?: "",
                object : CameraDevice.StateCallback() {
                    override fun onOpened(device: CameraDevice) {
                        this@CameraCore.cameraDevice = device
                        it.onNext(true)
                        it.onComplete()
                    }

                    override fun onDisconnected(camera: CameraDevice) {
                        it.onNext(false)
                        it.onComplete()
                    }

                    override fun onError(camera: CameraDevice, error: Int) {
                        it.onNext(false)
                        it.onComplete()
                    }
                },
                null
            )
        }
    }

    private fun previewNow() {
        requestBuilder?.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        requestBuilder?.build()?.let { captureSession?.setRepeatingRequest(it, null, null) }
    }

    private fun chooseSizeFitWithScreen(choices: Array<Size>): Size {
        val screenRatio =
            RootApplication.screenSize.width.toFloat() / RootApplication.screenSize.height.toFloat()

        for (size in choices) {
            val sizeRatio = size.height.toFloat() / size.width.toFloat()
            if (screenRatio == sizeRatio) {
                return size
            }
        }

        for (size in choices) {
            if (size.width >= RootApplication.screenSize.height && size.height >= RootApplication.screenSize.width) {
                return size
            }
        }

        return choices[choices.size - 1]
    }
}
