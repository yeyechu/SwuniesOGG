package com.swu.dimiz.ogg.ui.myact.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.swu.dimiz.ogg.databinding.WindowPostCompletedBinding

class PostCompletedDialog : DialogFragment() {

    private var _binding : WindowPostCompletedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = WindowPostCompletedBinding.inflate(inflater, container, false)

        binding.buttonExit.setOnClickListener {
            dismiss()
        }

        binding.buttonCompleted.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "CompletedDialog"
    }
}