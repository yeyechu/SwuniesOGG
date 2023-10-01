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

class BadgeDetailWindow : Fragment() {

    private var _binding: WindowBadgeDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BadgeListViewModel by activityViewModels { BadgeListViewModel.Factory }
    private lateinit var fragmentManager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.window_badge_detail, container, false)

        fragmentManager = childFragmentManager

        binding.buttonCompleted.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}