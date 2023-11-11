package com.swu.dimiz.ogg

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class OggLifecycleObserver(private val registry: ActivityResultRegistry): DefaultLifecycleObserver {

    private lateinit var getResult: ActivityResultLauncher<Intent>

    override fun onCreate(owner: LifecycleOwner) {
        getResult = registry.register("key", owner, ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK) {
                val data = it.data?.getStringExtra("result")
            }
        }
    }

    fun buttonClicked() {
//        val intent = Intent(context, CameraActivity::class.java)
//        getResult.launch("")
    }
}