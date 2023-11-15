package com.swu.dimiz.ogg.ui.feed.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.swu.dimiz.ogg.OggSnackbar
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentFeedDetailWhiteBinding
import com.swu.dimiz.ogg.ui.feed.FeedViewModel
import timber.log.Timber

class FeedDetailFragment : Fragment() {

    private var _binding: FragmentFeedDetailWhiteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FeedViewModel by activityViewModels()

    private lateinit var navController: NavController
    private lateinit var fragmentManager: FragmentManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_feed_detail_white, container, false)
        Timber.i("onCreateView()")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        fragmentManager = childFragmentManager

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.buttonExit.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            viewModel.onFeedDetailCompleted()
        }

        viewModel.makeToasts.observe(viewLifecycleOwner) {
            when(it) {
                1 -> {
                    OggSnackbar.make(view, getText(R.string.feed_toast_reaction_to_mine).toString()).show()
                    viewModel.onToastCompleted()
                }
                2 -> {
                    OggSnackbar.make(view, getText(R.string.feed_toast_reaction_already).toString()).show()
                    viewModel.onToastCompleted()
                }
                3 -> {
                    OggSnackbar.make(view, getText(R.string.feed_toast_report_to_mine).toString()).show()
                    viewModel.onToastCompleted()
                }
                4 -> {
                    OggSnackbar.make(view, getText(R.string.feed_toast_report_already).toString()).show()
                    viewModel.onToastCompleted()
                }
                5 -> {
                    OggSnackbar.make(view, getText(R.string.env_toast_badge).toString()).show()
                    viewModel.onToastCompleted()
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}