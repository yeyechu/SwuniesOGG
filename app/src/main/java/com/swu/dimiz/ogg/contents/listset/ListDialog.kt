package com.swu.dimiz.ogg.contents.listset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.swu.dimiz.ogg.databinding.DialogListBinding
import timber.log.Timber

class ListDialog : DialogFragment() {

    private var _binding : DialogListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListsetViewModel by activityViewModels { ListsetViewModel.Factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = DialogListBinding.inflate(inflater, container, false)

        binding.dialogLayout.setOnClickListener {  }

        binding.buttonLeft.setOnClickListener {
            onOnedayClicked()
            Timber.i("왼쪽 버튼 눌림")
        }

        binding.buttonRight.setOnClickListener {
            onLeftdayClicked()
            Timber.i("오른쪽 버튼 눌림")
        }

        binding.buttonExit.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    private fun onLeftdayClicked() {
        // 오른쪽 버튼 구현
        dismiss()
        viewModel.fireReSave()
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
    private fun onOnedayClicked() {
        // 왼쪽 버튼 구현
        dismiss()
        viewModel.fireOnlySave()
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ListDialog"
    }
}