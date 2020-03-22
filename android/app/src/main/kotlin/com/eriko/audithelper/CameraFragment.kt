package com.eriko.audithelper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.camera_fragment.*

class CameraFragment : Fragment(), View.OnClickListener {
    companion object {
        fun newInstance(): CameraFragment = CameraFragment()
    }

    private val co: CompositeDisposable = CompositeDisposable()

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.start_preview_btn -> {
                if (camera.isOpened) {
                    camera.close()
                } else {
                    camera.open()
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
        start_preview_btn.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (camera.isOpened) {
            camera.close()
        }
        camera.destroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        co.dispose()
    }
}
