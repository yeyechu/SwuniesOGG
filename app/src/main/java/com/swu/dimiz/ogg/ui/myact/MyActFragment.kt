package com.swu.dimiz.ogg.ui.myact

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.skydoves.balloon.*
import com.swu.dimiz.ogg.MainActivity
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentMyActBinding
import com.swu.dimiz.ogg.ui.myact.daily.PostDailyWindow
import com.swu.dimiz.ogg.ui.myact.extra.ExtraAdapter
import com.swu.dimiz.ogg.ui.myact.extra.PostExtraWindow
import com.swu.dimiz.ogg.ui.myact.sust.PostSustWindow
import com.swu.dimiz.ogg.ui.myact.sust.SustCardItemAdapter
import com.swu.dimiz.ogg.ui.myact.uploader.CameraActivity

class MyActFragment : Fragment() {

    private var _binding: FragmentMyActBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyActViewModel by activityViewModels { MyActViewModel.Factory }

    private lateinit var navController: NavController
    private lateinit var fragmentManager: FragmentManager

    private var balloonSus: Balloon? = null // Balloon 변수 추가
    private var balloonExtra: Balloon? = null

    private lateinit var cameraTitle: String
    private lateinit var cameraCo2: String
    private lateinit var cameraId: String
    private lateinit var cameraFilter: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                    기본 초기화
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_act, container, false)

        val mainActivity = activity as MainActivity

        //firebase
        //viewModel.fireInfo()
        //viewModel.fireGetDaily()
        viewModel.fireGetExtra()

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       어댑터

        viewModel.navigateToDaily.observe(viewLifecycleOwner) {
            if (it == null) {
                mainActivity.hideBottomNavView(false)
            } else {
                mainActivity.hideBottomNavView(true)
                addDailyWindow()
            }
        }

        viewModel.todailyId.observe(viewLifecycleOwner) {
            it?.let {
                cameraTitle = it.title
                cameraCo2 = it.co2.toString()
                cameraId = it.dailyId.toString()
                cameraFilter = it.filter
            }
        }

        binding.sustainableActList.adapter = SustCardItemAdapter(viewModel, SustCardItemAdapter.OnClickListener {
            viewModel.showSust(it)
        })

        viewModel.navigateToSust.observe(viewLifecycleOwner) {

            if (it == null) {
                mainActivity.hideBottomNavView(false)
            } else {
                mainActivity.hideBottomNavView(true)
                addSustWindow()
            }
        }

        binding.extraActList.adapter = ExtraAdapter(viewModel, ExtraAdapter.OnClickListener {
            viewModel.showExtra(it)
        })

        viewModel.navigateToExtra.observe(viewLifecycleOwner) {

            if (it == null) {
                mainActivity.hideBottomNavView(false)
            } else {
                mainActivity.hideBottomNavView(true)
                addExtraWindow()
            }
        }

        viewModel.sustId.observe(viewLifecycleOwner) {
            it?.let {
                cameraTitle = it.title
                cameraCo2 = it.co2.toString()
                cameraId = it.sustId.toString()
                cameraFilter = it.filter
            }
        }

        viewModel.extraId.observe(viewLifecycleOwner) {
            it?.let {
                cameraTitle = it.title
                cameraCo2 = it.co2.toString()
                cameraId = it.extraId.toString()
                cameraFilter = it.filter
            }
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                   tooltip
        binding.buttonTooltipSust.setOnClickListener {
            balloonSus?.showAlignBottom(binding.buttonTooltipSust)
        }

        binding.buttonTooltipExtra.setOnClickListener {
            balloonExtra?.showAlignBottom(binding.buttonTooltipExtra)
        }

        balloonSus = createBalloon(requireContext()) {
            setHeight(BalloonSizeSpec.WRAP)
            setWidth(BalloonSizeSpec.WRAP)
            setText(getString(R.string.myact_tooltip_sustainable))
            setTextColorResource(R.color.primary_blue)
            setTextSize(15f)
            setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            setArrowSize(10)
            setArrowPosition(0.5f)
            setPadding(12)
            setCornerRadius(8f)
            setBackgroundColorResource(R.color.white)
            setBalloonAnimation(BalloonAnimation.FADE)
            build()
        }
        balloonExtra = createBalloon(requireContext()) {
            setHeight(BalloonSizeSpec.WRAP)
            setWidth(BalloonSizeSpec.WRAP)
            setText(getString(R.string.myact_tooltip_extra))
            setTextColorResource(R.color.primary_blue)
            setTextSize(15f)
            setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            setArrowSize(10)
            setArrowPosition(0.5f)
            setPadding(12)
            setCornerRadius(8f)
            setBackgroundColorResource(R.color.white)
            setBalloonAnimation(BalloonAnimation.FADE)
            build()
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        fragmentManager = childFragmentManager

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.fireGetSust()

        viewModel.navigateToToCamera.observe(viewLifecycleOwner) {
            if(it) {
                val intent = Intent(context, CameraActivity::class.java)
                intent.putExtra("title", cameraTitle)
                intent.putExtra("co2", cameraCo2)
                intent.putExtra("id", cameraId)
                intent.putExtra("filter", cameraFilter)
                requireContext().startActivity(intent)
                viewModel.onCameraCompleted()
            }
        }

        viewModel.navigateToToGallery.observe(viewLifecycleOwner) {
            if(it) {
                startGallery()
                viewModel.onGalleryCompleted()
            }
        }

        viewModel.navigateToToChecklist.observe(viewLifecycleOwner) {
            if(it) {
                navController.navigate(MyActFragmentDirections.actionNavigationMyactToDestinationChecklist())
                viewModel.onChecklistCompleted()
                fragmentManager.popBackStack()
            }
        }

        viewModel.navigateToToCar.observe(viewLifecycleOwner) {
            if(it) {
                navController.navigate(MyActFragmentDirections.actionNavigationMyactToDestinationSettingCar())
                viewModel.onCarCompleted()
                fragmentManager.popBackStack()
            }
        }

        viewModel.navigateToToLink.observe(viewLifecycleOwner) {
            if(it) {
                // 와이어프레임이 아직 안나와서
                // 이동은 안함
                // 이동 정의 필요
                viewModel.onLinkCompleted()
                fragmentManager.popBackStack()
            }
        }
    }

    private fun startGallery() {
        getPicture.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val getPicture = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {
        if(it != null) {
            viewModel.setUri(it)
        }
    }

    private fun addDailyWindow() {
        fragmentManager.beginTransaction()
            .add(R.id.frame_layout_myact, PostDailyWindow())
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

    private fun addSustWindow() {
        fragmentManager.beginTransaction()
            .add(R.id.frame_layout_myact, PostSustWindow())
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

    private fun addExtraWindow() {
        fragmentManager.beginTransaction()
            .add(R.id.frame_layout_myact, PostExtraWindow())
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
