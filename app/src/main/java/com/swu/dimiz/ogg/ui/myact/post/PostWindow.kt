package com.swu.dimiz.ogg.ui.myact.post

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.databinding.WindowPostBinding
import com.swu.dimiz.ogg.oggdata.OggDatabase
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDailyDatabaseDao
import timber.log.Timber

class PostWindow : AppCompatActivity() {


    private lateinit var binding: WindowPostBinding
    private lateinit var intentPath: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = WindowPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentId = intent.getStringExtra("activity")
        val intentValid = intent.getStringExtra("activityValid")

        intentPath = intent.getStringExtra("pathValue").toString()
        Timber.i("전달된 Id 확인 : $intentId")

        val application = requireNotNull(this).application
        val dataActivity = OggDatabase.getInstance(application).dailyDatabaseDao
        val dataInstruction = OggDatabase.getInstance(application).instructionDatabaseDao

        val viewModelFactory = PostWindowViewModelFactory(intentId!!.toInt(), intentValid!!.toInt(), dataActivity, dataInstruction)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(PostWindowViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = TextAdapter()
        binding.textList.adapter = adapter

        if(intentPath == "LIST_ADAPTER") {
            Timber.i("onPostButton() $intentPath")
            viewModel.onPostButton()
        }

        viewModel.gettInstructions.observe(this, Observer {
            it?.let {
                adapter.data = it
            }
        })

        viewModel.showPostButton.observe(this) {
            if (it == true) {
                Timber.i("버튼 안보임")
                binding.buttonLayout.visibility = View.GONE
            } else {
                binding.buttonLayout.visibility = View.VISIBLE
            }
        }

        binding.buttonExit.setOnClickListener {
            finish()
        }
    }
}
