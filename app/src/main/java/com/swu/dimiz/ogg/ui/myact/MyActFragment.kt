package com.swu.dimiz.ogg.ui.myact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.skydoves.balloon.*
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentMyActBinding

class MyActFragment : Fragment() {

    private var _binding: FragmentMyActBinding? = null
    private val binding get() = _binding!!

    private var balloon_sus: Balloon? = null // Balloon 변수 추가
    private var balloon_extra: Balloon? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyActBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonTodayActEdit.setOnClickListener { view: View ->
            view.findNavController().navigate(
                MyActFragmentDirections
                    .actionNavigationMyactToDestinationListchanger()
            )
        }

        //tooltip 버튼
        binding.sustainableActInfo.setOnClickListener {
            balloon_sus?.showAlignBottom(binding.sustainableActInfo)

        }
        binding.extraActInfo.setOnClickListener {
            balloon_extra?.showAlignBottom(binding.extraActInfo)

        }


        // balloon 객체 생성
        balloon_sus = createBalloon(requireContext()) {
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
        balloon_extra = createBalloon(requireContext()) {
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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
