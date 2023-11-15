package com.swu.dimiz.ogg.ui.myact.uploader

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraActivity = this

        binding.buttonExit.setOnClickListener {

            setResult(RESULT_CANCELED)
            finish()
        }

        id = intent.getStringExtra("id")!!
        title = intent.getStringExtra("title")!!
        co2 = intent.getStringExtra("co2")!!
        filter = intent.getStringExtra("filter")!!
        postCount = intent.getStringExtra("postCount")!!

        binding.textTitle.text = title
        binding.textCo2.text = getString(R.string.post_text_co2, co2.toFloat())
        string = title.toString()
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
        var title: String = ""
        var string: String = ""
        var co2: String = ""
        var filter: String = ""
        var postCount: String = ""
    }

    override fun onDestroy() {
        cameraActivity = null
        super.onDestroy()
    }
}

const val KEY_EVENT_ACTION = "key_event_action"
const val KEY_EVENT_EXTRA = "key_event_extra"