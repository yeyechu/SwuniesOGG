package com.swu.dimiz.ogg.ui.myact.sust

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.WindowPostSustBinding
import com.swu.dimiz.ogg.ui.myact.MyActViewModel
import timber.log.Timber

class PostSustWindow : Fragment() {

    private var _binding : WindowPostSustBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyActViewModel by activityViewModels { MyActViewModel.Factory }
    private lateinit var fragmentManager: FragmentManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate( inflater, R.layout.window_post_sust, container, false)

        binding.buttonExit.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            viewModel.onNavigatedSust()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragmentManager = childFragmentManager
        Timber.i("지속 가능한 post")

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        //viewModel.fireGetSust()
    }

    override fun onStart() {
        super.onStart()
        Timber.i("onStart()")
    }
    override fun onResume() {
        super.onResume()
        Timber.i("onResume()")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.i("onDetach()")
    }
}