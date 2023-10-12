package com.swu.dimiz.ogg.ui.feed.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentFeedDetailBinding
import com.swu.dimiz.ogg.ui.feed.FeedViewModel

class FeedDetailFragment : Fragment() {

    private var _binding: FragmentFeedDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private val viewModel: FeedViewModel by activityViewModels()

    private lateinit var fragmentManager: FragmentManager


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_feed_detail, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        fragmentManager = childFragmentManager

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.buttonExit.setOnClickListener { item: View ->
            item.findNavController().navigateUp()
        }

        viewModel.navigateToReport.observe(viewLifecycleOwner) {
            it?.let {
                addWindow(it.id)
                viewModel.onReportCompleted()
            }
        }

        viewModel.makeToast.observe(viewLifecycleOwner) {
            if(it) {
                Toast.makeText(context, "휴먼, 너의 피드를 좋아요 할 수 없다", Toast.LENGTH_SHORT).show()
                viewModel.onYourFeedCompleted()
            }
        }
    }

    private fun addWindow(id: String) {
        fragmentManager.beginTransaction()
            .add(R.id.frame_layout_feed, FeedReportDialog(id))
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}