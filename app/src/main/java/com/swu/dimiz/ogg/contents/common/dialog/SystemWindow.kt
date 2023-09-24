package com.swu.dimiz.ogg.contents.common.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.WindowPopupSystemBinding

class SystemWindow : Fragment() {

    // DialogFragment로 구현할 예정

    companion object {
        fun newInstance() = SystemWindow()
    }
    private var _binding : WindowPopupSystemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate( inflater, R.layout.window_popup_system, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}