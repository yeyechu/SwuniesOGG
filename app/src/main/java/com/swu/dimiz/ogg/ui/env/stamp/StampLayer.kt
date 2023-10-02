package com.swu.dimiz.ogg.ui.env.stamp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.DATE_WHOLE
import com.swu.dimiz.ogg.contents.listset.StampData
import com.swu.dimiz.ogg.databinding.LayerStampBinding
import com.swu.dimiz.ogg.ui.env.EnvViewModel
import timber.log.Timber

class StampLayer : Fragment() {

    private var _binding: LayerStampBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EnvViewModel by viewModels({ requireParentFragment() })

    private lateinit var stampHolderAdapter: StampAdapter
    private var stampHolder = ArrayList<StampData>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.layer_stamp, container, false)

        val stampView: GridView = binding.stampGrid

        stampInitialize()
        viewModel.setStampHolder(stampHolder)

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                    d
        viewModel.stampHolder.observe(viewLifecycleOwner) {
            it?.let {
                stampHolderAdapter = StampAdapter(requireContext(), it)
                stampView.adapter = stampHolderAdapter
            }
        }

        viewModel.progressBar.observe(viewLifecycleOwner) {
            binding.progressBar.progress = it
        }

        return binding.root
    }

    private fun stampInitialize() {
        stampHolder.clear()
        var index = 1

        for (i in 1..DATE_WHOLE) {
            stampHolder.add(StampData(index++, 0f))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.condition = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}