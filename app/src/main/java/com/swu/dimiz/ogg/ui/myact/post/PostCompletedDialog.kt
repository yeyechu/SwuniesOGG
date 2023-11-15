package com.swu.dimiz.ogg.ui.myact.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.swu.dimiz.ogg.databinding.WindowPostCompletedBinding
import com.swu.dimiz.ogg.ui.myact.MyActViewModel

class PostCompletedDialog : Fragment() {

    private var _binding : WindowPostCompletedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyActViewModel by activityViewModels { MyActViewModel.Factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = WindowPostCompletedBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.buttonCompleted.setOnClickListener {
            viewModel.onPostOver()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}