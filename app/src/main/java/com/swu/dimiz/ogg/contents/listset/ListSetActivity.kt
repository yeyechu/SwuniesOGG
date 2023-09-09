package com.swu.dimiz.ogg.contents.listset

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.swu.dimiz.ogg.databinding.ActivityListSetBinding

class ListSetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListSetBinding

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        binding = ActivityListSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}