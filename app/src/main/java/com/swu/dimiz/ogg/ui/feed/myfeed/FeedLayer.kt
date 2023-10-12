package com.swu.dimiz.ogg.ui.feed.myfeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.LayerFeedBinding
import com.swu.dimiz.ogg.ui.feed.FeedViewModel
import timber.log.Timber

class FeedLayer : Fragment() {

    private var _binding: LayerFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FeedViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    : View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.layer_feed, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.feedListGrid.adapter = FeedGridAdapter(FeedGridAdapter.OnFeedClickListener {
            viewModel.onFeedDetailClicked(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}