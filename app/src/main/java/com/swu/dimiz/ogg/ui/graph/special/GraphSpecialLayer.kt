package com.swu.dimiz.ogg.ui.graph.special

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.LayerGraphSpecialCardBinding
import com.swu.dimiz.ogg.ui.graph.GraphViewModel
import timber.log.Timber

class GraphSpecialLayer: Fragment() {

    private var _binding: LayerGraphSpecialCardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GraphViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.layer_graph_special_card, container, false)

//        binding.feedListGrid.adapter = GraphAdapter(GraphAdapter.OnFeedClickListener {
//            viewModel.onFeedDetailClicked(it)
//        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}