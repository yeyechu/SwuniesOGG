package com.swu.dimiz.ogg.ui.myact.uploader

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.ActivityCameraBinding
import timber.log.Timber

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.i("onCreate()")

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraActivity = this

        binding.buttonExit.setOnClickListener {
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                val intent = Intent(KEY_EVENT_ACTION).apply {
                    putExtra(KEY_EVENT_EXTRA, keyCode)
                }
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    //                              수명 주기 체크
    override fun onStart() {
        super.onStart()
        Timber.i("onStart()")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume()")
    }

    override fun onPause() {
        super.onPause()
        Timber.i("onPause()")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy()")
    }

    companion object {
        var cameraActivity: CameraActivity? = null
    }
}

const val KEY_EVENT_ACTION = "key_event_action"
const val KEY_EVENT_EXTRA = "key_event_extra"