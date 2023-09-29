package com.swu.dimiz.ogg.contents.listset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentListBinding
import timber.log.Timber

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListsetViewModel by activityViewModels { ListsetViewModel.Factory }
    private lateinit var fragmentManager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list, container, false)

        fragmentManager = childFragmentManager
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                     카테고리 출력

        viewModel.activityFilter.observe(viewLifecycleOwner) { value ->
            val chipGroup = binding.activityFilter
            val chipInflater = LayoutInflater.from(chipGroup.context)

            val children = value.map { category ->
                val chip = chipInflater.inflate(R.layout.item_chips, chipGroup, false) as Chip
                chip.text = category
                chip.tag = category
                if (category == "energy") {
                    chip.isChecked = true
                }
                chip.setOnCheckedChangeListener { button, isChecked ->
                    viewModel.onFilterChanged(button.tag as String, isChecked)
                }
                chip
            }
            chipGroup.removeAllViews()

            for (chip in children) {
                chipGroup.addView(chip)
            }
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       어댑터
        val numList: ArrayList<ListData> = viewModel.numList
        val adapter = ListsetAdapter(numList,
            ListsetAdapter.ListClickListener {
                viewModel.co2Minus(it)
            },
            ListsetAdapter.ListClickListener {
                viewModel.co2Plus(it)
            },
            ListsetAdapter.ListClickListener {
                viewModel.showPopup(it)
            })
        binding.activityList.adapter = adapter

        viewModel.filteredList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner) {
            it?.let {
                addWindow()
                viewModel.completedPopup()
            }
        }
        return binding.root
    }

    private fun addWindow() {
        fragmentManager.beginTransaction()
            .add(R.id.frame_layout, ListDetailWindow())
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