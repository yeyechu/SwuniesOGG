package com.swu.dimiz.ogg.contents.common.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.WindowPopupSystemBinding

class SystemWindow(
    private val title: String,
    private val body: String,
    private val left: String,
    private val right: String) : DialogFragment() {

    private var _binding : WindowPopupSystemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate( inflater, R.layout.window_popup_system, container, false)

        binding.textPopupTitle.text = title
        binding.textPopupBody.text = body
        binding.buttonLeft.text = left
        binding.buttonRight.text = right

        binding.buttonLeft.setOnClickListener {
            onCancelClicked()
        }
        binding.buttonRight.setOnClickListener {
            onSubmitClicked()
        }

        return binding.root
    }

    private fun onSubmitClicked() {
        dismiss()
    }
    private fun onCancelClicked() {
        dismiss()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SystemDialog"
    }
}