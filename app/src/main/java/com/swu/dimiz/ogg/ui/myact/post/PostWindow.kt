package com.swu.dimiz.ogg.ui.myact.post

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.databinding.WindowPostBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDailyDatabaseDao
import timber.log.Timber

class PostWindow : AppCompatActivity() {

    private val viewModel: PostWindowViewModel by viewModels {
        PostWindowViewModelFactory((application as OggApplication).repository)
    }

    private lateinit var binding: WindowPostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = WindowPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentId = intent.getStringExtra("activity")
        Timber.i("전달된 Id 확인 : $intentId")

        binding.buttonExit.setOnClickListener {
            finish()
        }
    }
}
