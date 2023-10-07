package com.swu.dimiz.ogg.ui.myact.post

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.swu.dimiz.ogg.databinding.WindowPostBinding
import com.swu.dimiz.ogg.oggdata.OggDatabase
import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed
import com.swu.dimiz.ogg.ui.myact.uploader.CameraActivity
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class PostWindow : AppCompatActivity() {

    private lateinit var binding: WindowPostBinding

    private lateinit var intentPath: String

    private lateinit var uri: Uri

    @SuppressLint("SimpleDateFormat")
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

        viewModel.gettInstructions.observe(this) {
            it?.let {
                adapter.data = it
            }
        }

        viewModel.showPostButton.observe(this) {
            if (it == true) {
                Timber.i("버튼 안보임")
                binding.buttonLayout.visibility = View.GONE
            } else {
                binding.buttonLayout.visibility = View.VISIBLE
            }
        }

        binding.buttonRight.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
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

        val fireDB = Firebase.firestore
        val fireUser = Firebase.auth.currentUser
        val fireStorage = Firebase.storage

        binding.buttonDone.setOnClickListener {
            // post 데이터가 올라가야 함
            //스토리지와 피드 업로드
            val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(Date()) // 파일명이 겹치면 안되기 때문

            fireStorage.reference.child("Feed").child(fileName)
                .putFile(uri)              //uri를 여기서 받기때문에 여기에 위치함
                .addOnSuccessListener {
                        taskSnapshot -> // 업로드 정보를 담는다
                    Timber.i("feed storage 올리기 완료")
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                            it->
                        val imageUrl=it.toString()
                        val post = Feed( email = fireUser?.email.toString(),  postTime = fileName.toLong(), //활동코드 추가
                            imageUrl = imageUrl)

                        fireDB.collection("Feed").document(fileName)
                            .set(post)
                            .addOnCompleteListener { Timber.i("feed firestore 올리기 완료")
                            }.addOnFailureListener {  e -> Timber.i("feed firestore 올리기 오류", e)}
                    }
                }.addOnFailureListener {  e -> Timber.i("feed storage 올리기 오류", e)}
            //스탬프 수치 업로드
            //daily 상태 업로드
            //MyALLAct
            finish()
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getPicture.launch(intent)
        binding.previewLayout.visibility = View.VISIBLE
        binding.explainLayout.visibility = View.GONE

    }
    private val getPicture: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode == RESULT_OK && it.data != null) {
            uri = it.data!!.data!!

            Glide.with(this)
                .load(uri)
                .into(binding.imagePreview)
            //
        }
    }
    companion object{
        var postWindow : PostWindow? = null
    }
}
