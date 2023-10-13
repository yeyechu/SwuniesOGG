package com.swu.dimiz.ogg.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.listutils.TOGETHER
import com.swu.dimiz.ogg.databinding.FragmentFeedBinding
class FeedFragment : Fragment(){

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FeedViewModel by activityViewModels()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_feed, container, false)

        // 피드 디테일 갔다가
        // 다시 돌아오면
        // 원래대로 돌아가 있음
        // 초기화 다른데서 시키기
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
        navController = findNavController()

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.navigateToSelectedItem.observe(viewLifecycleOwner) {
            it?.let {
                navController.navigate(FeedFragmentDirections.actionNavigationFeedToDestinationFeedDetail())
                viewModel.onFeedDetailCompleted()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}