package com.swu.dimiz.ogg.ui.feed.myfeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.LayerMyFeedBinding
import com.swu.dimiz.ogg.oggdata.OggDatabase

class FeedLayer : Fragment() {

    private lateinit var binding: LayerMyFeedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.layer_my_feed, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = OggDatabase.getInstance(application).myPostDatabaseDao

        val viewModelFactory = FeedLayerViewModelFactory(dataSource, application)
        val feedLayerViewModel = ViewModelProvider(
            this, viewModelFactory).get(FeedLayerViewModel::class.java)

        binding.feedLayerViewModel = feedLayerViewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}