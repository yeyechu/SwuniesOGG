package com.swu.dimiz.ogg.ui.myact.uploader

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
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
        val intentTitle = intent.getStringExtra("title")
        val intentCo2 = intent.getStringExtra("co2")
        id = intent.getStringExtra("id")!!

        binding.textTitle.text = intentTitle
        binding.textCo2.text = getString(R.string.post_text_co2, intentCo2!!.toFloat())
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

    companion object {
        var cameraActivity: CameraActivity? = null
        var id: String = ""
    }
}

const val KEY_EVENT_ACTION = "key_event_action"
const val KEY_EVENT_EXTRA = "key_event_extra"