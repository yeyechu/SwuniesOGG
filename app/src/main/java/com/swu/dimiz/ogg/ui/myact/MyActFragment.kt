package com.swu.dimiz.ogg.ui.myact

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.swu.dimiz.ogg.ui.myact.myactcard.*
import com.swu.dimiz.ogg.ui.myact.sust.PostSustWindow
import com.swu.dimiz.ogg.ui.myact.sust.SustCardItemAdapter
import com.swu.dimiz.ogg.ui.myact.uploader.CameraActivity
import timber.log.Timber

class MyActFragment : Fragment() {

    private var _binding: FragmentMyActBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyActViewModel by activityViewModels { MyActViewModel.Factory }
    private lateinit var navController: NavController
    private lateinit var fragmentManager: FragmentManager

    private var balloonSus: Balloon? = null // Balloon 변수 추가
    private var balloonExtra: Balloon? = null

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
        viewModel.fireInfo()
        //viewModel.fireGetDaily()
        viewModel.fireGetSust()
        viewModel.fireGetExtra()

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       어댑터

        binding.sustainableActList.adapter = SustCardItemAdapter(SustCardItemAdapter.OnClickListener {
            viewModel.showPopup(it)
        })

        viewModel.navigateToSelected.observe(viewLifecycleOwner) {

            if (it == null) {
                mainActivity.hideBottomNavView(false)
            } else {
                mainActivity.hideBottomNavView(true)
                addWindow()
            }
        }

        val adapterextra = ExtraCardItemAdapter(requireContext())
        binding.extraActList.adapter = adapterextra

        viewModel.getExtraData.observe(viewLifecycleOwner) {
            Timber.i("$it")
            it?.let {
                adapterextra.submitList(it)
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

        viewModel.navigateToToCamera.observe(viewLifecycleOwner) {
            if(it) {
                requireContext().startActivity(Intent(context, CameraActivity::class.java))
                viewModel.onCameraCompleted()
            }
        }

        viewModel.navigateToToChecklist.observe(viewLifecycleOwner) {
            if(it) {
                navController.navigate(MyActFragmentDirections.actionNavigationMyactToDestinationChecklist())
                viewModel.onChechlistCompleted()
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
    }

    private fun addWindow() {
        fragmentManager.beginTransaction()
            .add(R.id.frame_layout_myact, PostSustWindow())
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
