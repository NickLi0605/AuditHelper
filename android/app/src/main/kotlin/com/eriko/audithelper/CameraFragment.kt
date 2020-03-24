package com.eriko.audithelper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.fragment.app.Fragment
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.camera_fragment.*
import com.googlecode.tesseract.android.TessBaseAPI

class CameraFragment : Fragment(), View.OnClickListener {
    companion object {
        fun newInstance(): CameraFragment = CameraFragment()
    }

    private val co: CompositeDisposable = CompositeDisposable()

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.capture_btn -> {
                if (camera.isOpened) {
                    camera.takePicture()
                }
            }
            else -> Log.e(TAG, "Unknown id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.camera_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        capture_btn.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        openCamera()
    }

    override fun onPause() {
        super.onPause()
        camera.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        camera.destroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        co.dispose()
    }

    private fun openCamera() {
        if (!camera.isOpened) {
            camera.open()
            camera.addCameraListener(object : CameraListener() {
                override fun onPictureTaken(result: PictureResult) {
                    super.onPictureTaken(result)
                    result.toBitmap {
                        val baseApi = TessBaseAPI()
                        baseApi.init("/sdcard/Download/tesseract/", "eng")
                        baseApi.pageSegMode = TessBaseAPI.PageSegMode.PSM_SINGLE_LINE
                        baseApi.setImage(it)
                        val outputText = baseApi.utF8Text
                        ocr_result.text = outputText
                        capture_view.setImageBitmap(it)
                    }
                }
            })
        }
    }
}
