package com.swu.dimiz.ogg.ui.myact.post

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.WindowPostBinding
import com.swu.dimiz.ogg.oggdata.OggDatabase
import com.swu.dimiz.ogg.ui.myact.uploader.CameraActivity
import com.swu.dimiz.ogg.ui.myact.uploader.CameraFragment
import timber.log.Timber

class PostWindow : AppCompatActivity() {

    private lateinit var binding: WindowPostBinding

    private lateinit var intentPath: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = WindowPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postWindow = this

        val intentId = intent.getStringExtra("activity")
        val intentValid = intent.getStringExtra("activityValid")

        intentPath = intent.getStringExtra("pathValue").toString()

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

        binding.buttonRight.setOnClickListener {
            val intent : Intent = Intent(this, CameraActivity::class.java)
//            intent.putExtra("titleActivity", extraTitle)
//            intent.putExtra("co2Activity", activityCo2)
            this.startActivity(intent)
            Timber.i("카메라 시작 버튼 눌림")
        }

        binding.buttonExit.setOnClickListener {
            finish()
        }

        binding.buttonLeft.setOnClickListener {
            startGallery()
        }

        binding.buttonRetake.setOnClickListener {
            startGallery()
        }

        binding.buttonDone.setOnClickListener {
            finish()
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getPicture.launch(intent)
        binding.previewLayout.visibility = View.VISIBLE
    }
    private val getPicture: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode == RESULT_OK && it.data != null) {
            val uri = it.data!!.data

            Glide.with(this)
                .load(uri)
                .into(binding.imagePreview)
        }
    }
    companion object{
        var postWindow : PostWindow? = null
    }
}
