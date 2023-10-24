package com.swu.dimiz.ogg.ui.myact.extra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.WindowPostExtraBinding
import com.swu.dimiz.ogg.ui.myact.MyActViewModel
import timber.log.Timber

class PostExtraWindow : Fragment() {

    private var _binding : WindowPostExtraBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyActViewModel by activityViewModels { MyActViewModel.Factory }
    private lateinit var fragmentManager: FragmentManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate( inflater, R.layout.window_post_extra, container, false)

        binding.buttonExit.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            viewModel.completedExtra()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragmentManager = childFragmentManager
        Timber.i("특별 post")

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}