package com.swu.dimiz.ogg.ui.env.myenv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.LayerEnvBinding

class MyEnvLayer : Fragment() {

    private lateinit var binding: LayerEnvBinding
//    private var _binding: LayerEnvBinding? = null
//    private val binding get() = _binding!!

    // 뷰모델+레포지토리 구현

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate( inflater, R.layout.layer_env, container, false)

        binding.buttonSave.setOnClickListener { view: View ->
            view.findNavController().navigateUp()
        }

        // ────────────────────────────────────────────────────────────────────────────────────────
        //                                    BottomSheet
        val bottomBehavior = BottomSheetBehavior.from(binding.bottomLayout)
        bottomBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomBehavior.apply {
            addBottomSheetCallback(object  : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when(newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {}
                        BottomSheetBehavior.STATE_EXPANDED -> {}
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {}
                        BottomSheetBehavior.STATE_HIDDEN -> {}
                        BottomSheetBehavior.STATE_DRAGGING -> {}
                        BottomSheetBehavior.STATE_SETTLING -> {}
                    }
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.bottomLayout.apply {
                    }
                }
            })
        }

        //                                 BottomSheet 핸들러
        binding.handler.setOnClickListener {
            bottomBehavior.state = if(bottomBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                BottomSheetBehavior.STATE_EXPANDED
            } else {
                BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //_binding = null
    }
}