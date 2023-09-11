package com.swu.dimiz.ogg.ui.env.stamp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.LayerStampBinding

class StampLayer : Fragment() {

    private lateinit var binding : LayerStampBinding

    // 라이브데이터 + 뷰모델 필요

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.layer_stamp, container, false)

        binding.beforeLayer.visibility = View.VISIBLE

        return binding.root
    }
}