package com.swu.dimiz.ogg.ui.myact.uploader

import android.annotation.SuppressLint
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
import android.util.Log
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.convertDurationToInt
import com.swu.dimiz.ogg.convertToDuration
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
    private val fireUser = Firebase.auth.currentUser
    private val fireStorage = Firebase.storage

    private var startDate = 0L
    var today = 0
    private var projectCount = 0

    private var feedDay = ""

    // ─────────────────────────────────────────────────────────────────────────────────
    //                                      기타
    //var bitmap : Bitmap? = null

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        Timber.i("카메라 onCreateView()")



        fireDB.collection("User").document(fireUser?.email.toString())
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

        binding.buttonRetake.setOnClickListener {
            binding.previewLayout.visibility = View.GONE
        }

        binding.buttonDone.setOnClickListener {

            CameraActivity.cameraActivity!!.finish()

            feedDay = System.currentTimeMillis().toString()


            // ─────────────────────────────────────────────────────────────────────────────────
            //                           세가지 활동 분리해서 업로드
            if(CameraActivity.id.toInt() < 20000){
                //Daily
                updateDailyPostCount()
                val daily = MyDaily(
                    dailyID = CameraActivity.id.toInt(),
                    upDate = feedDay.toLong()
                )
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Project${projectCount}").document("Daily")
                    .collection(today.toString()).document(feedDay)
                    .set(daily)
                    .addOnSuccessListener { Timber.i("Daily firestore 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }else if(CameraActivity.id.toInt() < 30000){
                //Sustainable
                updateSustPostDate()
                val sust = MySustainable(
                    sustID = CameraActivity.id.toInt(),
                    strDay = feedDay.toLong(),
                )
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Sustainable").document(CameraActivity.id)
                    .set(sust)
                    .addOnSuccessListener { Timber.i("Sustainable firestore 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }else{
                //Extra
                updateExtraPostDate()
                val extra = MyExtra(
                    extraID = CameraActivity.id.toInt(),
                    strDay = feedDay.toLong(),
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
            updateBageCate()
            updateBadgeAct()
            updateBadgeCo2()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return binding.root
    }

    private fun updateDailyPostCount() = lifecycleScope.launch {
        var count = CameraActivity.postCount.toInt()
        val id = CameraActivity.id.toInt()
        withContext(Dispatchers.IO) {
            OggDatabase.getInstance(requireContext())
                .dailyDatabaseDao
                .updatePostCountFromFirebase(
                    id,
                    ++count
                )
        }
    }

    private fun updateSustPostDate() = lifecycleScope.launch {
        withContext(Dispatchers.IO) {
            OggDatabase.getInstance(requireContext())
                .sustDatabaseDao
                .updateSustDateFromFirebase(
                    CameraActivity.id.toInt(),
                    System.currentTimeMillis()
                )
        }
    }

    private fun updateExtraPostDate() = lifecycleScope.launch {
        withContext(Dispatchers.IO) {
            OggDatabase.getInstance(requireContext())
                .extraDatabaseDao
                .updateExtraDateFromFirebase(
                    CameraActivity.id.toInt(),
                    System.currentTimeMillis()
                )
        }
    }
    // ─────────────────────────────────────────────────────────────────────────────────
    //                               인증사진 피드 업로드
    private fun feedUpload(){
        if(savedUri!=null) {
            fireStorage.reference.child("Feed").child(feedDay)
                .putFile(savedUri!!)          //uri를 여기서 받기때문에 여기에 위치함
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                            it->
                        val imageUrl=it.toString()
                        val post = Feed(
                            email = fireUser?.email.toString(),
                            actTitle = CameraActivity.string,
                            postTime = feedDay.toLong(),
                            actId = CameraActivity.id.toInt(),
                            actCode = CameraActivity.filter,
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
    private fun updateAllAct(){
        //AllAct
        val washingtonRef = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project${projectCount}").document("Entire")
            .collection("AllAct").document(CameraActivity.id)
        washingtonRef
            .update("upCount", FieldValue.increment(1))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
        washingtonRef
            .update("allCo2", FieldValue.increment(CameraActivity.co2.toDouble()))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i( e ) }
    }

    // ─────────────────────────────────────────────────────────────────────────────────
    //                              스탬프 업데이트
    private fun updateStamp(){
        if(CameraActivity.id.toInt() < 20000){
            //daily
            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Project${projectCount}").document("Entire")
                .collection("Stamp").document(today.toString())
                .update("dayCo2", FieldValue.increment(CameraActivity.co2.toDouble()))
                .addOnSuccessListener { Timber.i("Stamp firestore 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }

        }else if(CameraActivity.id.toInt() < 30000){
            //sust
            for( i in today..21){
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Project${projectCount}").document("Entire")
                    .collection("Stamp").document(i.toString())
                    .update("dayCo2", FieldValue.increment(CameraActivity.co2.toDouble()))
                    .addOnSuccessListener { Timber.i("Stamp firestore 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }
        }
        else{
            //extra  특별활동 순위 비교 위함
            fireDB.collection("User").document(fireUser?.email.toString())
                .update("extraPost", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("extraPost 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────────
    //                              배지 카운트 업데이트
    private fun updateBageCate(){
        when(CameraActivity.id.toInt()){
            //에너지
            10001,10002,10003,10004,10005,10006,10007,10008,
            20001,20002,20003,20004,20005,20006,
            30004,30005 ->{
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Badge").document("40007")
                    .update("count", FieldValue.increment(1))
                    .addOnSuccessListener { Timber.i("40007 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }
            //소비
            10009,30006,30007 ->{
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Badge").document("40008")
                    .update("count", FieldValue.increment(1))
                    .addOnSuccessListener { Timber.i("40008 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }
            //이동수단
            10010,10011,10012,
            20007,20008 ->{
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Badge").document("40009")
                    .update("count", FieldValue.increment(1))
                    .addOnSuccessListener { Timber.i("40009 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }

            //자원순환
            10013,10014,10015,10016,10017,10018,10019,10020,
            30001,30002,30003 ->{
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Badge").document("40010")
                    .update("count", FieldValue.increment(1))
                    .addOnSuccessListener { Timber.i("40010 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }
            }
        }
    }

    private fun updateBadgeAct(){
        when(CameraActivity.id.toInt()){
            //sust
            20001 -> fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document("40014")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40014 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            20002 -> fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document("40015")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40015 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            20003 -> fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document("40016")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40016 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            20004 -> fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document("40017")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40017 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            20005 -> fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document("40018")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40018 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            20006 -> fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document("40019")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40019 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            20007 -> fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document("40020")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40020 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            20008 -> fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document("40021")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40021 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }

            //extra
            30001 -> fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document("40025")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40025 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            30002 -> fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document("40026")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40026 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            30003 -> fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document("40027")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40027 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            30004 -> fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document("40028")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40028 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            30005 -> fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document("40029")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40029 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            30006 -> fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document("40030")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40030 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            30007 -> fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Badge").document("40031")
                .update("count", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("40031 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
        }
    }

    private fun updateBadgeCo2(){
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

    // ─────────────────────────────────────────────────────────────────────────────────
    //                              배지 획득 이벤트
    private fun updateBadgeDate(){
        fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Badge")
            .orderBy("badgeID")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }

                val cities = ArrayList<String>()
                for (doc in value!!) {
                    doc.getString("name")?.let {
                        cities.add(it)
                    }
                }
            }

    }


    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()
        broadcastManager = LocalBroadcastManager.getInstance(view.context)

        val filter = IntentFilter().apply { addAction(KEY_EVENT_ACTION) }
        broadcastManager.registerReceiver(volumeDownReceiver, filter)

        binding.viewFinder.post {
            displayId = binding.viewFinder.display.displayId
            lifecycleScope.launch {
                setUpCamera()
                Timber.i("카메라 실행")
            }
        }
    }

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
                        val appName = requireContext().resources.getString(R.string.app_name_korean)
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/${appName}")
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
        super.onDestroyView()
        Timber.i("카메라 onDestroyView()")
        cameraExecutor.shutdown()
        broadcastManager.unregisterReceiver(volumeDownReceiver)
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