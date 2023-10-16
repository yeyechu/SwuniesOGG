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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.swu.dimiz.ogg.convertDurationToInt
import com.swu.dimiz.ogg.databinding.WindowPostBinding
import com.swu.dimiz.ogg.oggdata.OggDatabase
import com.swu.dimiz.ogg.oggdata.remotedatabase.*
import com.swu.dimiz.ogg.ui.myact.uploader.CameraActivity
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class PostWindow : AppCompatActivity() {

    private lateinit var binding: WindowPostBinding

    private lateinit var intentPath: String

    private lateinit var uri: Uri

    val fireDB = Firebase.firestore
    val fireUser = Firebase.auth.currentUser
    val fireStorage = Firebase.storage


    var startDate : Long = 0L
    var today :Int = 0

    private var feedDay = ""
    var stampDay = ""

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


        fireDB.collection("User").document(fireUser?.email.toString())
            .get().addOnSuccessListener { document ->
                if (document != null) {
                    val gotUser = document.toObject<MyCondition>()
                    gotUser?.let {
                        startDate = gotUser.startDate
                        today = convertDurationToInt(startDate)
                    }
                } else { Timber.i("사용자 기본정보 받아오기 실패") }
            }.addOnFailureListener { exception -> Timber.i(exception.toString()) }

        binding.buttonDone.setOnClickListener {
            feedDay = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
            stampDay = SimpleDateFormat("yyyyMMdd").format(Date())

            // ─────────────────────────────────────────────────────────────────────────────────
            //                           세가지 활동 분리해서 업로드
            if(CameraActivity.id.toInt() < 20000){
                //Daily
                val daily = MyDaily(
                    dailyID = CameraActivity.id.toInt(),
                    upDate = feedDay.toLong(),
                    Limit =0,
                    doLeft = 0
                )
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Daily").document(stampDay)
                    .set(daily)
                    .addOnSuccessListener { Timber.i("Daily firestore 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }else if( CameraActivity.id.toInt() < 30000){
                //Sustainable
                val sust = MySustainable(
                    sustID = CameraActivity.id.toInt(),
                    strDay = feedDay.toLong(),
                    Limit =0,
                    dayLeft = 0
                )
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Sustainable").document(CameraActivity.id)
                    .set(sust)
                    .addOnSuccessListener { Timber.i("Sustainable firestore 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }else{
                //Extra
                val extra = MyExtra(
                    extraID = CameraActivity.id.toInt(),
                    strDay = feedDay.toLong(),
                    Limit =0,
                    dayLeft = 0
                )
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Extra").document(CameraActivity.id)
                    .set(extra)
                    .addOnSuccessListener { Timber.i("Extra firestore 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }
            feedUpload()
            updateAllAct()
            updateStamp()

            finish()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────────
    //                               인증사진 피드 업로드
    private fun feedUpload(){
        //스토리지와 피드 업로드
        fireStorage.reference.child("Feed").child(feedDay)
            .putFile(uri)              //uri를 여기서 받기때문에 여기에 위치함
            .addOnSuccessListener {
                    taskSnapshot -> // 업로드 정보를 담는다
                Timber.i("feed storage 올리기 완료")
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                        it->
                    val imageUrl=it.toString()
                    val post = Feed(
                        email = fireUser?.email.toString(),
                        actTitle =  CameraActivity.title,
                        postTime = feedDay.toLong(),
                        actId = CameraActivity.id.toInt(),
                        actCode = CameraActivity.filter,
                        imageUrl = imageUrl)

                    fireDB.collection("Feed").document(feedDay)
                        .set(post)
                        .addOnCompleteListener { Timber.i("feed firestore 올리기 완료")
                        }.addOnFailureListener {  e -> Timber.i("feed firestore 올리기 오류", e)}
                }
            }.addOnFailureListener {  e -> Timber.i("feed storage 올리기 오류", e)}
    }

    // ─────────────────────────────────────────────────────────────────────────────────
    //                              활동 전체 상황 업로드
    private fun updateAllAct(){
        //AllAct
        val washingtonRef = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("AllAct").document(CameraActivity.id)
        washingtonRef
            .update("upCount", FieldValue.increment(1))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
        washingtonRef
            .update("allC02", FieldValue.increment(CameraActivity.co2.toDouble()))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
        washingtonRef
            .update("actCode", CameraActivity.filter)
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
    }

    // ─────────────────────────────────────────────────────────────────────────────────
    //                              스탬프 업데이트
    private fun updateStamp(){
        if(CameraActivity.id.toInt() < 20000){
            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Stamp").document(today.toString())
                .update("dayCo2", FieldValue.increment(CameraActivity.co2.toDouble()))
                .addOnSuccessListener { Timber.i("Stamp firestore 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }

        }else{
            //todo limit가져와서 범위 변경
            for( i in today..21){
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Stamp").document(i.toString())
                    .update("dayCo2", FieldValue.increment(CameraActivity.co2.toDouble()))
                    .addOnSuccessListener { Timber.i("Stamp firestore 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }
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
