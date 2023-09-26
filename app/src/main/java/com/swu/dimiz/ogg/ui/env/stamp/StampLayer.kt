package com.swu.dimiz.ogg.ui.env.stamp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.LayerStampBinding
import com.swu.dimiz.ogg.ui.env.EnvViewModel
import timber.log.Timber

class StampLayer : Fragment() {

    private lateinit var binding : LayerStampBinding

    private val viewModel: EnvViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                    기본 초기화

        binding = DataBindingUtil.inflate(
            inflater, R.layout.layer_stamp, container, false)

        binding.condition = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}