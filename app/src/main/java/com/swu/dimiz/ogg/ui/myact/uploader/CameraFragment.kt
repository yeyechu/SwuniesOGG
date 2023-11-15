package com.swu.dimiz.ogg.ui.myact.uploader

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.concurrent.futures.await
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.swu.dimiz.ogg.*
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.listutils.DATE_WHOLE
import com.swu.dimiz.ogg.contents.listset.listutils.ID_MODIFIER
import com.swu.dimiz.ogg.databinding.FragmentCameraBinding
import com.swu.dimiz.ogg.oggdata.OggDatabase
import com.swu.dimiz.ogg.oggdata.remotedatabase.*
import com.swu.dimiz.ogg.ui.myact.uploader.utils.ANIMATION_FAST_MILLIS
import com.swu.dimiz.ogg.ui.myact.uploader.utils.ANIMATION_SLOW_MILLIS
import com.swu.dimiz.ogg.ui.myact.uploader.utils.simulateClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    // ─────────────────────────────────────────────────────────────────────────────────
    //                                    카메라 변수
    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var cameraExecutor : ExecutorService

    private var cameraProvider: ProcessCameraProvider? = null
    private var displayId: Int = -1
    private var imageCapture : ImageCapture? = null
    private var preview: Preview? = null
    private var camera: Camera? = null
    private var savedUri: Uri? = null

    // ─────────────────────────────────────────────────────────────────────────────────
    //                                  firebase 변수
    private val fireDB = Firebase.firestore
    private val fireStorage = Firebase.storage

    private val userEmail = OggApplication.auth.currentUser!!.email.toString()
    private var userCondition: MyCondition = MyCondition()
    private var badgeId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUserCondition()
        cameraExecutor = Executors.newSingleThreadExecutor()
        broadcastManager = LocalBroadcastManager.getInstance(view.context)

        val filter = IntentFilter().apply { addAction(KEY_EVENT_ACTION) }
        broadcastManager.registerReceiver(volumeDownReceiver, filter)

        val actId = CameraActivity.id.toInt()
        val actTitle = CameraActivity.string
        val actCo2 = CameraActivity.co2.toDouble()
        val actFilter = CameraActivity.filter
        val actCount = CameraActivity.postCount.toInt()

        binding.viewFinder.post {
            displayId = binding.viewFinder.display.displayId
            lifecycleScope.launch {
                setUpCamera()
            }
        }

        binding.buttonRetake.setOnClickListener {
            binding.previewLayout.visibility = View.GONE
        }

        binding.buttonDone.setOnClickListener {

            Timber.i("인증버튼 내 사용자 정보 확인 : $userCondition")
            Timber.i("활동 아이디: $actId, 활동 횟수: $actCount")
            val currentTime = System.currentTimeMillis()
            val today = convertToDuration(userCondition.startDate)
            val projectCount = userCondition.projectCount

            feedUpload(currentTime, actId, actTitle, actFilter)
            updateAllAct(projectCount, actId, actCo2)
            updateStamp(projectCount, today, actId, actCo2)
            updateBageCate(actId)

            when(actId / ID_MODIFIER) {
                1 -> {
                    updateDailyToFirebase(currentTime, projectCount, today, actId)
                    updateDailyPostCount(actId, actCount + 1)
                    updateBadgeCo2(actId, actCo2)
                }
                2 -> {
                    updateSustToFirebase(currentTime, actId)
                    updateSustPostDate(actId, currentTime)
                    updateBadgeSust(actId)
                }
                3 -> {
                    updateExtraToFirebase(currentTime, actId)
                    updateExtraPostDate(actId, currentTime)
                    updateBadgeExtra(actId)
                }
            }

            updateBadgeDate(currentTime)
            updateBadgeDateCo2(currentTime)

            val result = Intent().apply {
                putExtra("badgeId", badgeId)
            }
            CameraActivity.cameraActivity!!.apply {
                setResult(Activity.RESULT_OK, result)

                if(!isFinishing) {
                    finish()
                }
            }
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initUserCondition() = lifecycleScope.launch {
        fireDB.collection("User").document(userEmail)
            .get().addOnSuccessListener { document ->
                if (document != null) {
                    val gotUser = document.toObject<MyCondition>()
                    gotUser?.let {
                        userCondition = gotUser
                    }
                } else {
                    Timber.i("사용자 기본정보 받아오기 실패")
                }
            }.addOnFailureListener { exception ->
                Timber.i(exception.toString())
            }
        Timber.i("카메라 사용자 정보: $userCondition")
    }

    private fun updateDailyToFirebase(date: Long, num: Int, today: Int, id: Int) = lifecycleScope.launch {
        val daily = MyDaily(
            dailyID = id,
            upDate = date
        )
        fireDB.collection("User").document(userEmail)
            .collection("Project${num}").document("Daily")
            .collection(today.toString()).document(date.toString())
            .set(daily)
            .addOnSuccessListener { Timber.i("Daily firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
    }

    private fun updateSustToFirebase(date: Long, id: Int) = lifecycleScope.launch {
        val sust = MySustainable(
            sustID = id,
            strDay = date,
        )
        fireDB.collection("User").document(userEmail)
            .collection("Sustainable").document(id.toString())
            .set(sust)
            .addOnSuccessListener { Timber.i("Sustainable firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
    }

    private fun updateExtraToFirebase(date: Long, id: Int) = lifecycleScope.launch {
        val extra = MyExtra(
            extraID = id,
            strDay = date,
        )
        fireDB.collection("User").document(userEmail)
            .collection("Extra").document(id.toString())
            .set(extra)
            .addOnSuccessListener { Timber.i("Extra firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
    }

    private fun updateDailyPostCount(id: Int, postCount: Int) = lifecycleScope.launch {
        withContext(Dispatchers.IO) {
            OggDatabase.getInstance(requireContext())
                .dailyDatabaseDao
                .updatePostCountFromFirebase(
                    id,
                    postCount + 1
                )
        }
    }

    private fun updateSustPostDate(id: Int, date: Long) = lifecycleScope.launch {
        withContext(Dispatchers.IO) {
            OggDatabase.getInstance(requireContext())
                .sustDatabaseDao
                .updateSustDateFromFirebase(
                    id,
                    date
                )
        }
    }

    private fun updateExtraPostDate(id: Int, date: Long) = lifecycleScope.launch {
        withContext(Dispatchers.IO) {
            OggDatabase.getInstance(requireContext())
                .extraDatabaseDao
                .updateExtraDateFromFirebase(
                    id,
                    date
                )
        }
    }
    // ─────────────────────────────────────────────────────────────────────────────────
    //                               인증사진 피드 업로드
    private fun feedUpload(date: Long, id: Int, title: String, filter: String){
        if(savedUri != null) {
            fireStorage.reference.child("Feed").child(date.toString())
                .putFile(savedUri!!)          //uri를 여기서 받기때문에 여기에 위치함
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                            it->
                        val imageUrl=it.toString()
                        val post = Feed(
                            email = userEmail,
                            actTitle = title,
                            postTime = date,
                            actId = id,
                            actCode = filter,
                            imageUrl = imageUrl
                        )
                        fireDB.collection("Feed").document()
                            .set(post)
                            .addOnCompleteListener { Timber.i("feed firestore 올리기 완료")
                            }.addOnFailureListener {  e -> Timber.i("feed firestore 올리기 오류", e)}
                    }
                }.addOnFailureListener {  e -> Timber.i("feed storage 올리기 오류", e)}
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────────
    //                              활동 전체 상황 업로드
    private fun updateAllAct(num: Int, id: Int, co2: Double){
        //AllAct
        val washingtonRef = fireDB.collection("User").document(userEmail)
            .collection("Project${num}").document("Entire")
            .collection("AllAct").document(id.toString())
        washingtonRef
            .update("upCount", FieldValue.increment(1))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
        washingtonRef
            .update("allCo2", FieldValue.increment(co2))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
    }

    // ─────────────────────────────────────────────────────────────────────────────────
    //                              스탬프 업데이트
    private fun updateStamp(num: Int, today: Int, id: Int, co2: Double){
        when(id / ID_MODIFIER) {
            1 -> {
                fireDB.collection("User").document(userEmail)
                    .collection("Project${num}").document("Entire")
                    .collection("Stamp").document(today.toString())
                    .update("dayCo2", FieldValue.increment(co2))
                    .addOnSuccessListener { Timber.i("Stamp firestore 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }
            2 -> {
                for( i in today..DATE_WHOLE){
                    fireDB.collection("User").document(userEmail)
                        .collection("Project${num}").document("Entire")
                        .collection("Stamp").document(i.toString())
                        .update("dayCo2", FieldValue.increment(co2))
                        .addOnSuccessListener { Timber.i("Stamp firestore 올리기 완료") }
                        .addOnFailureListener { e -> Timber.i( e ) }
                }
            }
            3 -> {
                fireDB.collection("User").document(userEmail)
                    .update("extraPost", FieldValue.increment(1))
                    .addOnSuccessListener { Timber.i("extraPost 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────────
    //                              배지 카운트 업데이트  todo (1000곱하기)
    private fun updateBageCate(id: Int){
        when(id){
            //에너지
            10001,10002,10003,10004,10005,10006,10007,10008,
            20001,20002,20003,20004,20005,20006,
            30004,30005 ->{
                fireDB.collection("User").document(userEmail)
                    .collection("Badge").document("40007")
                    .update("count", FieldValue.increment(1))
                    .addOnSuccessListener { Timber.i("40007 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }
            //소비
            10009,30006,30007 ->{
                fireDB.collection("User").document(userEmail)
                    .collection("Badge").document("40008")
                    .update("count", FieldValue.increment(1))
                    .addOnSuccessListener { Timber.i("40008 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }
            //이동수단
            10010,10011,10012,
            20007,20008 ->{
                fireDB.collection("User").document(userEmail)
                    .collection("Badge").document("40009")
                    .update("count", FieldValue.increment(1))
                    .addOnSuccessListener { Timber.i("40009 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }

            //자원순환
            10013,10014,10015,10016,10017,10018,10019,10020,
            30001,30002,30003 ->{
                fireDB.collection("User").document(userEmail)
                    .collection("Badge").document("40010")
                    .update("count", FieldValue.increment(1))
                    .addOnSuccessListener { Timber.i("40010 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }
        }
    }

    private fun updateBadgeSust(id : Int){
        when(id){
            20001 -> fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40014")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40014 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            20002 -> fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40015")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40015 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            20003 -> fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40016")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40016 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            20004 -> fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40017")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40017 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            20005 -> fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40018")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40018 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            20006 -> fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40019")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40019 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            20007 -> fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40020")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40020 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            20008 -> fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40021")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40021 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
        }
    }

    private fun updateBadgeExtra(id : Int){
        when(id){
            30001 -> fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40025")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40025 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            30002 -> fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40026")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40026 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            30003 -> fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40027")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40027 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            30004 -> fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40028")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40028 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            30005 -> fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40029")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40029 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            30006 -> fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40030")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40030 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            30007 -> fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40031")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40031 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
        }
    }

    private fun updateBadgeCo2(id: Int, co2: Double){
        // 오늟의 활동만
        if(id < 20000) {
            fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40022")
                .update("count", FieldValue.increment(co2 * 1000))
                .addOnSuccessListener { Timber.i("40022 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40023")
                .update("count", FieldValue.increment(co2 * 1000))
                .addOnSuccessListener { Timber.i("40023 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            fireDB.collection("User").document(userEmail)
                .collection("Badge").document("40024")
                .update("count", FieldValue.increment(co2 * 1000))
                .addOnSuccessListener { Timber.i("40024 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────────
    //                              배지 획득 이벤트
    private fun updateBadgeDate(getDate: Long){
        val counts = ArrayList<MyBadge>()
        counts.clear()

        fireDB.collection("User").document(userEmail)
            .collection("Badge")
            .orderBy("badgeID")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Timber.i("${document.id} => ${document.data}")
                    val gotBadge = document.toObject<MyBadge>()
                    counts.add(gotBadge)
                }
                for(i in 0 until counts.size){
                    //카테고리
                    if(counts[6].count == 100 && counts[6].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40007")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40007 획득 완료")
                                badgeId = 40007
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    } else if(counts[7].count == 100 && counts[7].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40008")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40008 획득 완료")
                                badgeId = 40008
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    } else if(counts[8].count == 100 && counts[8].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40009")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40009 획득 완료")
                                badgeId = 40009
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    } else if(counts[9].count == 100 && counts[9].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40010")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40010 획득 완료")
                                badgeId = 40010
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }

                    //sust
                    if(counts[13].count == 1 && counts[13].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40014")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40014 획득 완료")
                                badgeId = 40014
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    } else if(counts[14].count == 1 && counts[14].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40015")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40015 획득 완료")
                                badgeId = 40015
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    } else if(counts[15].count == 1 && counts[15].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40016")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40016 획득 완료")
                                badgeId = 40016
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                    else if(counts[16].count == 1 && counts[16].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40017")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40017 획득 완료")
                                badgeId = 40017
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                    else if(counts[17].count == 1 && counts[17].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40018")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40018 획득 완료")
                                badgeId = 40018
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                    else if(counts[18].count == 1 && counts[18].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40019")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40019 획득 완료")
                                badgeId = 40019
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                    else if(counts[19].count == 1 && counts[19].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40020")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40020 획득 완료")
                                badgeId = 40020
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                    else if(counts[20].count == 1 && counts[20].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40021")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40021 획득 완료")
                                badgeId = 40021
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }

                    //extra
                    if(counts[24].count == 100 && counts[24].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40025")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40025 획득 완료")
                                badgeId = 40025
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                    else if(counts[25].count == 1 && counts[25].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40026")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40026 획득 완료")
                                badgeId = 40026
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                    else if(counts[26].count == 1 && counts[26].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40027")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40027 획득 완료")
                                badgeId = 40027
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                    else if(counts[27].count == 100 && counts[27].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40028")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40028 획득 완료")
                                badgeId = 40028
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                    else if(counts[28].count == 30 && counts[28].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40029")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40029 획득 완료")
                                badgeId = 40029
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                    else if(counts[29].count == 30 && counts[29].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40030")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40030 획득 완료")
                                badgeId = 40030
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                    else if(counts[30].count == 30 && counts[30].getDate == null){
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40031")
                            .update("getDate", getDate)
                            .addOnSuccessListener {
                                Timber.i("40031 획득 완료")
                                badgeId = 40031
                            }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Timber.i(exception)
            }
    }

    private fun updateBadgeDateCo2(getDate: Long){
        fireDB.collection("User").document(userEmail)
            .collection("Badge").document("40022")
            .addSnapshotListener { snapshot, e ->
                if (e != null) { Timber.i(e)
                    return@addSnapshotListener }
                if (snapshot != null && snapshot.exists()) {
                    val gotBadge = snapshot.toObject<MyBadge>()
                    if (gotBadge!!.count >= 100000 && gotBadge.getDate == null) {
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40022")
                            .update("getDate", getDate)
                            .addOnSuccessListener { Timber.i("40022 획득 완료") }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                } else { Timber.i("Current data: null") }
            }
        fireDB.collection("User").document(userEmail)
            .collection("Badge").document("40023")
            .addSnapshotListener { snapshot, e ->
                if (e != null) { Timber.i(e)
                    return@addSnapshotListener }
                if (snapshot != null && snapshot.exists()) {
                    val gotBadge = snapshot.toObject<MyBadge>()
                    if (gotBadge!!.count >= 500000 && gotBadge.getDate == null) {
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40023")
                            .update("getDate", getDate)
                            .addOnSuccessListener { Timber.i("40023 획득 완료") }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                } else { Timber.i("Current data: null") }
            }
        fireDB.collection("User").document(userEmail)
            .collection("Badge").document("40024")
            .addSnapshotListener { snapshot, e ->
                if (e != null) { Timber.i(e)
                    return@addSnapshotListener }
                if (snapshot != null && snapshot.exists()) {
                    val gotBadge = snapshot.toObject<MyBadge>()
                    if (gotBadge!!.count >= 1000000 && gotBadge.getDate == null) {
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40024")
                            .update("getDate", getDate)
                            .addOnSuccessListener { Timber.i("40024 획득 완료") }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                } else { Timber.i("Current data: null") }
            }
    }

    // ─────────────────────────────────────────────────────────────────────────────────
    //                              카메라 정의
    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun setUpCamera() {
        cameraProvider = ProcessCameraProvider.getInstance(requireContext()).await()

        bindCameraUseCases()
        takePictures()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun takePictures() {

        binding.buttonShutter.setOnClickListener {
            imageCapture?.let { imageCapture ->

                val name = SimpleDateFormat(FILENAME, Locale.KOREA)
                    .format(System.currentTimeMillis())

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(MediaStore.MediaColumns.MIME_TYPE, PHOTO_TYPE)

                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/${requireContext().resources.getString(R.string.app_name_korean)}")
                    }
                }

                val outputOptions = ImageCapture.OutputFileOptions
                    .Builder(requireContext().contentResolver,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues)
                    .build()

                imageCapture.takePicture(
                    outputOptions, cameraExecutor, object: ImageCapture.OnImageSavedCallback {
                        override fun onError(exception: ImageCaptureException) {
                            Timber.tag(TAG).e(exception, "사진 찍기 실패 : %s", exception.message)
                        }

                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            savedUri = outputFileResults.savedUri
                            Timber.tag(TAG).d("사진 찍기 성공: %s", savedUri)

                            setGalleryThumbnail(savedUri.toString())

                            binding.root.post{
                                binding.previewLayout.visibility = View.VISIBLE
                            }
                        }
                    })

                 //플래시 효과
                binding.root.postDelayed({
                    binding.root.foreground = ColorDrawable(Color.WHITE)
                    binding.root.postDelayed(
                        { binding.root.foreground = null }, ANIMATION_FAST_MILLIS)
                }, ANIMATION_SLOW_MILLIS)
            }
        }

    }

    private fun setGalleryThumbnail(filename: String) {

        binding.imagePreview.let { photoView ->
            photoView.post {
                photoView.setPadding(resources.getDimension(R.dimen.stroke_shape_bold).toInt())

                Glide.with(photoView)
                    .load(filename)
                    .apply(RequestOptions.fitCenterTransform())
                    .into(photoView)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun bindCameraUseCases() {

        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("카메라 초기화 실패")

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        preview = Preview.Builder().build()
        imageCapture = ImageCapture.Builder().build()

        cameraProvider.unbindAll()

        if(camera != null) {
            removeCameraStateObservers(camera!!.cameraInfo)
        }

        try{
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture
            )
            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            observeCameraState(camera?.cameraInfo!!)
        } catch (exc : Exception) {
            Timber.i("카메라 바인딩 실패")
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.i("카메라 onResume()")
        if(!PemissionsFragment.hasPermissions(requireContext())) {
            Timber.i("카메라 onResume() : 권한 없음")
            Navigation.findNavController(requireActivity(), R.id.camera_container).navigate(
                CameraFragmentDirections.actionDestinationCameraToDestinationPemissions()
            )
        }
    }

    override fun onDestroyView() {
        _binding = null
        cameraExecutor.shutdown()
        broadcastManager.unregisterReceiver(volumeDownReceiver)
        preview = null
        imageCapture = null
        savedUri = null
        cameraProvider = null
        camera = null
        super.onDestroyView()
        Timber.i("카메라 onDestroyView()")
    }

    private val volumeDownReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(KEY_EVENT_EXTRA, KeyEvent.KEYCODE_UNKNOWN)) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    binding.buttonShutter.simulateClick()
                }
            }
        }
    }

    private fun removeCameraStateObservers(cameraInfo: CameraInfo) {
        cameraInfo.cameraState.removeObservers(viewLifecycleOwner)
    }

    private fun observeCameraState(cameraInfo: CameraInfo) {
        cameraInfo.cameraState.observe(viewLifecycleOwner) { cameraState ->
            run {
                when (cameraState.type) {
                    CameraState.Type.PENDING_OPEN -> {
                        Timber.i("카메라 기다리는 중")
                    }
                    CameraState.Type.OPENING -> {
                        Timber.i("카메라 여는 중")
                    }
                    CameraState.Type.OPEN -> {
                        Timber.i("카메라 실행")
                    }
                    CameraState.Type.CLOSING -> {
                        Timber.i("카메라 닫는 중")
                    }
                    CameraState.Type.CLOSED -> {
                        Timber.i("카메라 종료")
                    }
                }
            }

            cameraState.error?.let { error ->
                when (error.code) {

                    CameraState.ERROR_STREAM_CONFIG -> {
                        Timber.i("configuration 오류, use case 확인 필요")
                    }
                    CameraState.ERROR_CAMERA_IN_USE -> {
                        Timber.i("열린 카메라 있음")
                    }
                    CameraState.ERROR_MAX_CAMERAS_IN_USE -> {
                        Timber.i("최대 카메라 사용 중")
                    }
                    CameraState.ERROR_OTHER_RECOVERABLE_ERROR -> {
                        Timber.i("recoverable 에러")
                    }
                    CameraState.ERROR_CAMERA_DISABLED -> {
                        Timber.i("카메라 사용 불가")
                    }
                    CameraState.ERROR_CAMERA_FATAL_ERROR -> {
                        Timber.i("카메라 복구를 위한 재부팅 필요")
                    }
                    CameraState.ERROR_DO_NOT_DISTURB_MODE_ENABLED -> {
                        Timber.i("방해금지 모드 해제 필요")
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "CameraX"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_TYPE = "image/jpeg"
    }
}