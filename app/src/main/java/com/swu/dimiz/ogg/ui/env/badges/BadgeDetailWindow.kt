package com.swu.dimiz.ogg.ui.env.badges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.WindowBadgeDetailBinding
import timber.log.Timber

class BadgeDetailWindow : Fragment() {

    private var _binding: WindowBadgeDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BadgeListViewModel by activityViewModels() { BadgeListViewModel.Factory }

    private lateinit var fragmentManager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.window_badge_detail, container, false)

        fragmentManager = childFragmentManager

        binding.buttonCompleted.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        viewModel.navigateToSelected.observe(viewLifecycleOwner) {

        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        Timber.i("배지 디테일")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("프래그먼트 백그라운드")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.i("프래그먼트 완벽 삭제")
    }

}