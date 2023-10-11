package com.swu.dimiz.ogg.ui.feed.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.swu.dimiz.ogg.databinding.DialogFeedReportBinding

class FeedReportDialog(private val feedId: Long) : DialogFragment() {

    private var _binding : DialogFeedReportBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = DialogFeedReportBinding.inflate(inflater, container, false)

        binding.buttonLeft.setOnClickListener {
            dismiss()
        }
        binding.buttonRight.setOnClickListener {
            onSubmitClicked()
        }

        return binding.root
    }

    private fun onSubmitClicked() {
        updateReport(/*인자자리*/)
        dismiss()
    }

    private fun updateReport(/*인자자리*/) {
        // 파이어베이스 함수 정의
        // 인자가 필요하다면 추가해주시면 됩니다
        Toast.makeText(context, "신고 되브렀어", Toast.LENGTH_SHORT).show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "FeedReportDialog"
    }
}