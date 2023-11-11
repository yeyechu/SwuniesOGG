package com.swu.dimiz.ogg.ui.myact

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.skydoves.balloon.*
import com.swu.dimiz.ogg.MainActivity
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentMyActBinding
import com.swu.dimiz.ogg.ui.myact.sust.PagerSustAdapter
import com.swu.dimiz.ogg.ui.myact.daily.PostDailyWindow
import com.swu.dimiz.ogg.ui.myact.extra.PagerExtraAdapter
import com.swu.dimiz.ogg.ui.myact.extra.PostExtraWindow
import com.swu.dimiz.ogg.ui.myact.sust.PostSustWindow
import com.swu.dimiz.ogg.ui.myact.uploader.CameraActivity

class MyActFragment : Fragment() {

    private var _binding: FragmentMyActBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyActViewModel by activityViewModels { MyActViewModel.Factory }

    private lateinit var navController: NavController
    private lateinit var fragmentManager: FragmentManager

    private lateinit var sustAdapter: PagerSustAdapter
    private lateinit var sustPager: ViewPager2

    private lateinit var extraAdapter: PagerExtraAdapter
    private lateinit var extraPager: ViewPager2

    private lateinit var cameraTitle: String
    private lateinit var cameraCo2: String
    private lateinit var cameraId: String
    private lateinit var cameraFilter: String
    private lateinit var cameraCount: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_act, container, false)

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                   tooltip
        val balloonSust = createBalloon(requireContext()) {
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
        val balloonExtra = createBalloon(requireContext()) {
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

        binding.buttonTooltipSust.setOnClickListener {
            balloonSust.showAlignBottom(binding.buttonTooltipSust)
        }

        binding.buttonTooltipExtra.setOnClickListener {
            balloonExtra.showAlignBottom(binding.buttonTooltipExtra)
        }

        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val mainActivity = activity as MainActivity

        navController = findNavController()
        navController.setLifecycleOwner(viewLifecycleOwner)

        fragmentManager = childFragmentManager

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.fireGetSust()
        viewModel.fireGetExtra()
        viewModel.fireGetDaily()

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                      지속가능한 어댑터
        sustAdapter = PagerSustAdapter(viewModel, PagerSustAdapter.OnClickListener{
            viewModel.showSust(it)
        })
        viewModel.getSustData.observe(viewLifecycleOwner) {
            sustAdapter.submitList(it)
        }
        sustPager = binding.pagerSust
        sustPager.adapter = sustAdapter

        TabLayoutMediator(binding.pagerIndicatorSust, sustPager) { tab, _->
            tab.text = ""
        }.attach()

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       특별 활동 어댑터

        extraAdapter = PagerExtraAdapter(viewModel, PagerExtraAdapter.OnClickListener {
            viewModel.showExtra(it)
        })

        viewModel.getExtraData.observe(viewLifecycleOwner) {
            extraAdapter.submitList(it)
        }
        extraPager = binding.pagerExtra
        extraPager.adapter = extraAdapter

        TabLayoutMediator(binding.pagerIndicatorExtra, extraPager) { tab, _->
            tab.text = ""
        }.attach()

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                      오늘의 활동 이동
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
                cameraCount = it.postCount.toString()
            }
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       지속 활동 이동
        viewModel.navigateToSust.observe(viewLifecycleOwner) {

            if (it == null) {
                mainActivity.hideBottomNavView(false)
            } else {
                mainActivity.hideBottomNavView(true)
                addSustWindow()
            }
        }

        viewModel.sustId.observe(viewLifecycleOwner) {
            it?.let {
                cameraTitle = it.title
                cameraCo2 = it.co2.toString()
                cameraId = it.sustId.toString()
                cameraFilter = it.filter
                cameraCount = "0"
            }
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       특별 활동 이동
        viewModel.navigateToExtra.observe(viewLifecycleOwner) {

            if (it == null) {
                mainActivity.hideBottomNavView(false)
            } else {
                mainActivity.hideBottomNavView(true)
                addExtraWindow()
            }
        }

        viewModel.extraId.observe(viewLifecycleOwner) {
            it?.let {
                cameraTitle = it.title
                cameraCo2 = it.co2.toString()
                cameraId = it.extraId.toString()
                cameraFilter = it.filter
                cameraCount = "0"
            }
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       인증 버튼 이동
        viewModel.navigateToToCamera.observe(viewLifecycleOwner) {
            if(it) {
                val intent = Intent(context, CameraActivity::class.java).apply {
                    putExtra("title", cameraTitle)
                    putExtra("co2", cameraCo2)
                    putExtra("id", cameraId)
                    putExtra("filter", cameraFilter)
                    putExtra("postCount", cameraCount)
                }

                //requireContext().startActivity(intent)
                activityResultLauncher.launch(intent)
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
                navController.navigate(MyActFragmentDirections.actionNavigationMyactToDestinationPostLink())
                viewModel.onLinkCompleted()
                fragmentManager.popBackStack()
            }
        }
    }

    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) {
            val data = it.data?.getStringExtra("result")
            viewModel.onPostCongrats()
        }
    }
    private fun startGallery() {
        getPicture.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val getPicture = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {
        it?.let {
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
