package com.eriko.audithelper

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Log
import android.view.TextureView
import android.view.Surface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.camera_fragment.*

class CameraFragment : Fragment() {
    companion object {
        fun newInstance(): CameraFragment = CameraFragment()
    }

    private lateinit var cameraCore: CameraCore
    private lateinit var previewTexture: TextureView
    private val co: CompositeDisposable = CompositeDisposable()
    private var running = false
    private val surfaceListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            preview()
        }
        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {}

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) = Unit

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?) = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.camera_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraCore = CameraCore()
        previewTexture = preview_texture
        start_preview_btn.setOnClickListener {
            if (running) {
                cameraCore.close()
                start_preview_btn.setImageResource(android.R.drawable.presence_video_busy)
            } else {
                context?.let {
                    when (checkSelfPermission(it, CAMERA)) {
                        PERMISSION_GRANTED -> {
                            preview()
                            start_preview_btn.setImageResource(R.drawable.ic_stop)
                        }
                        else -> Log.e(TAG, "Camera permission is not granted")
                    }
                }
            }
            running = !running
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        co.dispose()
    }

    private fun preview() {
        if (previewTexture.isAvailable) {
            cameraCore.openCamera().flatMap {
                if (!it) {
                    return@flatMap Observable.just(false)
                }
                previewTexture.surfaceTexture.setDefaultBufferSize(cameraCore.previewSize.width, cameraCore.previewSize.height)
                return@flatMap cameraCore.startPreview(arrayListOf(Surface(previewTexture.surfaceTexture)))
            }.subscribe {
                Log.d(TAG, "Preview: $it")
            }.addTo(co)
        } else {
            previewTexture.surfaceTextureListener = surfaceListener
        }
    }
}
