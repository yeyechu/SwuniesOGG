package com.swu.dimiz.ogg.ui.myact.daily

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.convertDurationToInt
import com.swu.dimiz.ogg.databinding.WindowPostDailyBinding
import com.swu.dimiz.ogg.oggdata.OggRepository
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

    private lateinit var fireDB : FirebaseFirestore
    private lateinit var fireStorage: FirebaseStorage
    private lateinit var uri: Uri
    private var today = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.window_post_daily, container, false)

        fireDB = Firebase.firestore
        fireStorage = Firebase.storage
        userInit()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            uploadPostToFirebase()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            viewModel.completedDaily()
            viewModel.resetUri()
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        fragmentManager = childFragmentManager
        Timber.i("포스트 데일리")
    }

    private fun userInit() {
        var startDate : Long = 0L

        fireDB.collection("User").document(OggApplication.auth.currentUser!!.email.toString())
            .get().addOnSuccessListener { document ->
                if (document != null) {
                    val gotUser = document.toObject<MyCondition>()
                    gotUser?.let {
                        startDate = gotUser.startDate
                        today = convertDurationToInt(startDate)
                    }
                } else { Timber.i("사용자 기본정보 받아오기 실패") }
            }.addOnFailureListener { exception -> Timber.i(exception.toString()) }
    }

    private fun uploadPostToFirebase() {

        val feedDay = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val stampDay = SimpleDateFormat("yyyyMMdd").format(Date())

        fireStorage.reference.child("Feed").child(feedDay)
            .putFile(uri)              //uri를 여기서 받기때문에 여기에 위치함
            .addOnSuccessListener {
                    taskSnapshot -> // 업로드 정보를 담는다
                Timber.i("feed storage 올리기 완료")
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                        it->
                    val imageUrl=it.toString()
                    val post = Feed(
                        email = OggApplication.auth.currentUser!!.email.toString(),
                        actTitle =  viewModel.todailyId.value!!.title,
                        postTime = feedDay.toLong(),
                        actId = viewModel.todailyId.value!!.dailyId,
                        actCode = viewModel.todailyId.value!!.filter,
                        imageUrl = imageUrl)

                    fireDB.collection("Feed").document(feedDay)
                        .set(post)
                        .addOnCompleteListener { Timber.i("feed firestore 올리기 완료")
                        }.addOnFailureListener {  e -> Timber.i("feed firestore 올리기 오류", e)}
                }
            }.addOnFailureListener {  e -> Timber.i("feed storage 올리기 오류", e)}

        val daily = MyDaily(
            dailyID = viewModel.todailyId.value!!.dailyId,
            upDate = feedDay.toLong(),
            Limit =0,
            doLeft = 0
        )
        fireDB.collection("User").document(OggApplication.auth.currentUser!!.email.toString())
            .collection("Daily").document(stampDay)
            .set(daily)
            .addOnSuccessListener { Timber.i("Daily firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
        //스탬프
        fireDB.collection("User").document(OggApplication.auth.currentUser!!.email.toString())
            .collection("Stamp").document(today.toString())
            .update("dayCo2", FieldValue.increment(viewModel.todailyId.value!!.co2.toDouble()))
            .addOnSuccessListener { Timber.i("Stamp firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }

        // ─────────────────────────────────────────────────────────────────────────────────
        //                              활동 전체 상황 업로드
        val washingtonRef = fireDB.collection("User").document(OggApplication.auth.currentUser!!.email.toString())
            .collection("AllAct").document(viewModel.todailyId.value!!.dailyId.toString())
        washingtonRef
            .update("upCount", FieldValue.increment(1))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
        washingtonRef
            .update("allC02", FieldValue.increment(viewModel.todailyId.value!!.co2.toDouble()))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
        washingtonRef
            .update("actCode", viewModel.todailyId.value!!.filter)
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}