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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.swu.dimiz.ogg.OggApplication.Companion.auth
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentCameraBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyStamp
import com.swu.dimiz.ogg.ui.myact.post.PostWindow
import com.swu.dimiz.ogg.ui.myact.uploader.utils.ANIMATION_FAST_MILLIS
import com.swu.dimiz.ogg.ui.myact.uploader.utils.ANIMATION_SLOW_MILLIS
import com.swu.dimiz.ogg.ui.myact.uploader.utils.simulateClick
import kotlinx.coroutines.launch
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


    // ─────────────────────────────────────────────────────────────────────────────────
    //                                      기타
    //var bitmap : Bitmap? = null

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        Timber.i("카메라 onCreateView()")

        binding.buttonRetake.setOnClickListener {
            binding.previewLayout.visibility = View.GONE
        }

        binding.buttonDone.setOnClickListener {

            //PostWindow.postWindow!!.finish()
            CameraActivity.cameraActivity!!.finish()


            Timber.i("post 데이터가 올라가야 함")
            val feedDay = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
            val stampDay = SimpleDateFormat("yyyyMMdd").format(Date())

            //todo 플젝 시작했다면 매일 새로 생성되는 쪽으로 이동해야함
           /* var stamp = MyStamp(upDate = feedDay.toLong(), dayCo2 = 0f)

            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Stamp").document(stampDay)
                .set(stamp)
                .addOnSuccessListener { Timber.i("Stamp firestore 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }*/

            /* 10000이면 daily, 20000이면 sustain, 30000이면 extra
            MySus, Myextra 각 테이블 따로 분리해서 업로드하기
            MyAllAct 활동 limit 체크

            */

            // 활동 아이디 ▼
            // CameraActivity.id
            if(savedUri!=null) {
                fireStorage.reference.child("Feed").child(feedDay)
                    .putFile(savedUri!!)          //uri를 여기서 받기때문에 여기에 위치함
                    .addOnSuccessListener {
                            taskSnapshot -> // 업로드 정보를 담는다
                        Timber.i("feed storage 올리기 완료")
                        taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                                it->
                            val imageUrl=it.toString()
                            val post = Feed(
                                email = fireUser?.email.toString(),
                                postTime = feedDay.toLong(),
                                actId = CameraActivity.id.toInt(),
                                //활동코드 추가
                                imageUrl = imageUrl)

                            fireDB.collection("Feed").document(feedDay)
                                .set(post)
                                .addOnCompleteListener { Timber.i("feed firestore 올리기 완료")
                                }.addOnFailureListener {  e -> Timber.i("feed firestore 올리기 오류", e)}
                        }
                    }.addOnFailureListener {  e -> Timber.i("feed storage 올리기 오류", e)}

                //스탬프 수치 업로드
                var itemCo2: Double = 0.1 //인증한 Co2

                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Stamp").document(stampDay)
                    .update("dayCo2", FieldValue.increment(itemCo2))
                    .addOnSuccessListener { Timber.i("Stamp firestore 올리기 완료") }
                    .addOnFailureListener { e -> Timber.i( e ) }

                //세활동 분리해서 저장
                if(CameraActivity.id.toInt() < 20000){

                }else if( CameraActivity.id.toInt() < 30000){

                }else{

                }

                //MyALLAct
            }
        }
        return binding.root
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