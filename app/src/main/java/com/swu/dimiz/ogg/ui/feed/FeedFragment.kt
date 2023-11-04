package com.swu.dimiz.ogg.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.swu.dimiz.ogg.MainActivity
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.listutils.TOGETHER
import com.swu.dimiz.ogg.databinding.FragmentFeedBinding
import com.swu.dimiz.ogg.ui.feed.detail.FeedDetailFragment
import com.swu.dimiz.ogg.ui.feed.detail.FeedReportDialog
import timber.log.Timber

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FeedViewModel by activityViewModels()

    private lateinit var navController: NavController
    private lateinit var fragmentManager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_feed, container, false)
        Timber.i("onCreateView()")

        viewModel.fireGetFeed()

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                     카테고리 출력
        viewModel.activityFilter.observe(viewLifecycleOwner) { value ->
            val chipGroup = binding.activityFilter
            val chipInflater = LayoutInflater.from(chipGroup.context)

            val children = value.map { category ->
                val chip = chipInflater.inflate(R.layout.item_chips, chipGroup, false) as Chip
                chip.text = category
                chip.tag = category
                if (category == TOGETHER) {
                    chip.isChecked = true
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    viewModel.onFilterChanged(TOGETHER, true)
                }
                chip.setOnCheckedChangeListener { button, isChecked ->

                    viewModel.onFilterChanged(button.tag as String, isChecked)
                    button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    if (!chip.isChecked) {
                        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary_light_gray))
                    }
                }
                chip
            }
            chipGroup.removeAllViews()

            for (chip in children) {
                chipGroup.addView(chip)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val mainActivity = activity as MainActivity

        navController = findNavController()
        navController.setLifecycleOwner(viewLifecycleOwner)

        fragmentManager = childFragmentManager

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.buttonNavigateTomyAct.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            it.findNavController().navigate(R.id.navigation_myact)
        }

        viewModel.navigateToSelectedItem.observe(viewLifecycleOwner) {
            if (it == null) {
                mainActivity.hideBottomNavView(false)
            } else {
                mainActivity.hideBottomNavView(true)
                addDetailFragment()
                Timber.i("디테일 열림")
            }
        }

        viewModel.navigateToReport.observe(viewLifecycleOwner) {
            it?.let {
                addReportWindow(it.id, it.email)
                viewModel.onReportCompleted()
            }
        }
    }

    private fun addDetailFragment() {
        fragmentManager.beginTransaction()
            .add(R.id.frame_layout_feed, FeedDetailFragment())
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

    private fun addReportWindow(id: String, email: String) {
        fragmentManager.beginTransaction()
            .add(R.id.frame_layout_feed, FeedReportDialog(id, email))
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}