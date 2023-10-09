package com.swu.dimiz.ogg.contents.listset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.WindowListDetailBinding
import com.swu.dimiz.ogg.ui.myact.post.TextAdapter
import timber.log.Timber

class ListDetailWindow : Fragment() {


    private var _binding: WindowListDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListsetViewModel by activityViewModels() { ListsetViewModel.Factory }

    private lateinit var fragmentManager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.window_list_detail, container, false)

        val adapter = TextAdapter()
        binding.textList.adapter = adapter


        binding.buttonExit.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        fragmentManager = childFragmentManager
        Timber.i("리스트 디테일")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}