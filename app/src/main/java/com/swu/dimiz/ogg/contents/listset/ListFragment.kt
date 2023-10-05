package com.swu.dimiz.ogg.contents.listset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.listutils.ENERGY
import com.swu.dimiz.ogg.contents.listset.listutils.ListsetAdapter
import com.swu.dimiz.ogg.databinding.FragmentListBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import timber.log.Timber

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListsetViewModel by activityViewModels { ListsetViewModel.Factory }
    private lateinit var cardView: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list, container, false
        )

        viewModel.toastVisibility.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(activity, getString(R.string.listset_text_toast), Toast.LENGTH_SHORT).show()
                viewModel.toastInvisible()
            }
        }
        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                     카테고리 출력

        viewModel.activityFilter.observe(viewLifecycleOwner) { value ->
            val chipGroup = binding.activityFilter
            val chipInflater = LayoutInflater.from(chipGroup.context)

            val children = value.map { category ->
                val chip = chipInflater.inflate(R.layout.item_chips, chipGroup, false) as Chip
                chip.text = category
                chip.tag = category
                if (category == ENERGY) {
                    chip.isChecked = true
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

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       어댑터
        val adapter = ListsetAdapter(
            ListsetAdapter.ListClickListener {
                viewModel.co2Minus(it)
                viewModel.deleteItem(it)
                viewModel.setListHolder(viewModel.listArray)
                Timber.i("${viewModel.listArray}")
            },
            ListsetAdapter.ListClickListener {
                viewModel.co2Plus(it)
                if (!viewModel.updateItem(it)) {
                    viewModel.addItem(it)
                }
                viewModel.setListHolder(viewModel.listArray)
                Timber.i("${viewModel.listArray}")
            },
            ListsetAdapter.ListClickListener {
                viewModel.showPopup(it)
            })

        binding.activityList.adapter = adapter

        viewModel.filteredList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}