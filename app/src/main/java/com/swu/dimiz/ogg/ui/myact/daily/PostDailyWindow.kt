package com.swu.dimiz.ogg.ui.myact.daily

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.convertDurationToInt
import com.swu.dimiz.ogg.convertToDuration
import com.swu.dimiz.ogg.databinding.WindowPostDailyBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.*
import com.swu.dimiz.ogg.ui.myact.MyActViewModel
import com.swu.dimiz.ogg.ui.myact.post.TextAdapter
import com.swu.dimiz.ogg.ui.myact.uploader.CameraActivity
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class PostDailyWindow  : Fragment() {

    private var _binding: WindowPostDailyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyActViewModel by activityViewModels { MyActViewModel.Factory }

    private lateinit var fragmentManager: FragmentManager

    private val fireDB  = Firebase.firestore
    private val fireUser = Firebase.auth.currentUser
    private val fireStorage = Firebase.storage
    private lateinit var uri: Uri
    private var today = 0
    private var projectCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.window_post_daily, container, false)

        userInit()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        fragmentManager = childFragmentManager

        val adapter = TextAdapter()
        binding.textList.adapter = adapter

        binding.buttonExit.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            viewModel.completedDaily()
            viewModel.resetUri()
        }

        viewModel.passUri.observe(viewLifecycleOwner) {

            if(it != null) {
                uri = it

                binding.previewLayout.visibility = View.VISIBLE
                binding.explainLayout.visibility = View.GONE

                Glide.with(this)
                    .load(it)
                    .into(binding.imagePreview)
            } else {
                binding.previewLayout.visibility = View.GONE
                binding.explainLayout.visibility = View.VISIBLE
            }
        }

        binding.buttonDone.setOnClickListener {
            viewModel.onPostCongrats()
            uploadPostToFirebase()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            viewModel.completedDaily()
            viewModel.resetUri()
        }
    }

    private fun userInit() {
        var startDate : Long

        fireDB.collection("User").document(OggApplication.auth.currentUser!!.email.toString())
            .get().addOnSuccessListener { document ->
                if (document != null) {
                    val gotUser = document.toObject<MyCondition>()
                    gotUser?.let {
                        startDate = gotUser.startDate
                        today = convertToDuration(startDate)
                        projectCount = gotUser.projectCount
                    }
                } else { Timber.i("사용자 기본정보 받아오기 실패") }
            }.addOnFailureListener { exception -> Timber.i(exception.toString()) }
    }

    private fun uploadPostToFirebase() {

        val feedDay = System.currentTimeMillis().toString()

        fireStorage.reference.child("Feed").child(feedDay)
            .putFile(uri)              //uri를 여기서 받기때문에 여기에 위치함
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                        it->
                    val imageUrl=it.toString()
                    val post = Feed(
                        email = OggApplication.auth.currentUser!!.email.toString(),
                        actTitle =  viewModel.todailyId.value!!.title,
                        postTime = feedDay.toLong(),
                        actId = viewModel.todailyId.value!!.dailyId,
                        actCode = viewModel.todailyId.value!!.filter,
                        imageUrl = imageUrl
                    )
                    fireDB.collection("Feed").document()
                        .set(post)
                        .addOnCompleteListener { Timber.i("feed firestore 올리기 완료")
                        }.addOnFailureListener {  e -> Timber.i("feed firestore 올리기 오류", e)}
                }
            }.addOnFailureListener {  e -> Timber.i("feed storage 올리기 오류", e)}

        val daily = MyDaily(
            dailyID = viewModel.todailyId.value!!.dailyId,
            upDate = feedDay.toLong(),
        )
        fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project${projectCount}").document("Daily")
            .collection(today.toString()).document(feedDay)
            .set(daily)
            .addOnSuccessListener { Timber.i("Daily firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
        //스탬프
        fireDB.collection("User").document(OggApplication.auth.currentUser!!.email.toString())
            .collection("Project${projectCount}").document("Entire")
            .collection("Stamp").document(today.toString())
            .update("dayCo2", FieldValue.increment(viewModel.todailyId.value!!.co2.toDouble()))
            .addOnSuccessListener { Timber.i("Stamp firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }

        // ─────────────────────────────────────────────────────────────────────────────────
        //                              활동 전체 상황 업로드
        val washingtonRef = fireDB.collection("User").document(OggApplication.auth.currentUser!!.email.toString())
            .collection("Project${projectCount}").document("Entire")
            .collection("AllAct").document(viewModel.todailyId.value!!.dailyId.toString())

        washingtonRef
            .update("upCount", FieldValue.increment(1))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }

        washingtonRef
            .update("allCo2", FieldValue.increment(viewModel.todailyId.value!!.co2.toDouble()))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }

        // ─────────────────────────────────────────────────────────────────────────────────
        //                              배지 카운트 업
        when(CameraActivity.id.toInt()){
            //에너지
            10001,10002,10003,10004,10005,10006,10007,10008 ->{
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Badge").document("40007")
                    .update("count", FieldValue.increment(1))
                    .addOnSuccessListener { Timber.i("40007 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }
            //소비
            10009 ->{
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Badge").document("40008")
                    .update("count", FieldValue.increment(1))
                    .addOnSuccessListener { Timber.i("40008 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }
            //이동수단
            10010,10011,10012 ->{
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Badge").document("40009")
                    .update("count", FieldValue.increment(1))
                    .addOnSuccessListener { Timber.i("40009 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }

            //자원순환
            10013,10014,10015,10016,10017,10018,10019,10020 ->{
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Badge").document("40010")
                    .update("count", FieldValue.increment(1))
                    .addOnSuccessListener { Timber.i("40010 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }
        }

        //배지 Co2
        fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Badge").document("40022")
            .update("count", FieldValue.increment(CameraActivity.co2.toDouble()))
            .addOnSuccessListener { Timber.i("40022 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
        fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Badge").document("40023")
            .update("count", FieldValue.increment(CameraActivity.co2.toDouble()))
            .addOnSuccessListener { Timber.i("40023 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
        fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Badge").document("40024")
            .update("count", FieldValue.increment(CameraActivity.co2.toDouble()))
            .addOnSuccessListener { Timber.i("40024 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}