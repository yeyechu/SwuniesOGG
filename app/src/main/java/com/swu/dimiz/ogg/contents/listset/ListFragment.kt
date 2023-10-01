package com.swu.dimiz.ogg.contents.listset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.listutils.ListsetAdapter
import com.swu.dimiz.ogg.databinding.FragmentListBinding
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import timber.log.Timber

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListsetViewModel by activityViewModels { ListsetViewModel.Factory }
    private var listHolder = ArrayList<ListData>()
    private var numberHolder = ArrayList<NumberData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list, container, false
        )

        listInitialize()
        viewModel.initCo2Holder()
        viewModel.setListHolder(listHolder)
        viewModel.setNumberHolder(numberHolder)
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
                deleteItem(it)
                viewModel.setListHolder(listHolder)
            },
            ListsetAdapter.ListClickListener {
                viewModel.co2Plus(it)
                if (!addItem(it)) {
                    updateItem(it)
                }
                viewModel.setListHolder(listHolder)
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

    private fun listInitialize() {
        listHolder.clear()
        numberHolder.clear()
        var index = ID_MODIFIER

        for (i in 0..4) {
            listHolder.add(ListData(0, 0))
        }

        for (i in 1..DATE_WHOLE) {
            numberHolder.add(NumberData(index++, 0))
        }
    }

    private fun addItem(item: ActivitiesDaily): Boolean {

        for (i in listHolder) {
            if (i.aId == item.dailyId) {
                if (item.freq < item.limit) {
                    i.aNumber++
                    numberHolder[item.dailyId - ID_MODIFIER] = NumberData(item.dailyId, i.aNumber)
                }
                Timber.i("$listHolder")
                Timber.i("$numberHolder")
                return true
            }
        }
        Timber.i("$listHolder")
        return false
    }

    private fun updateItem(item: ActivitiesDaily) {
        for (i in listHolder) {
            if (i.aId == 0) {
                i.aId = item.dailyId
                i.aNumber++
                numberHolder[item.dailyId - ID_MODIFIER] = NumberData(item.dailyId, i.aNumber)
                break
            }
        }
        Timber.i("$listHolder")
        Timber.i("$numberHolder")
    }

    private fun deleteItem(item: ActivitiesDaily) {
        for (i in listHolder) {
            if (i.aId == item.dailyId) {
                i.aNumber--
                numberHolder[item.dailyId - ID_MODIFIER] = NumberData(item.dailyId, i.aNumber)
                if (i.aNumber == 0) {
                    i.aId = 0
                }
            }
        }
        Timber.i("$listHolder")
        Timber.i("$numberHolder")
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

