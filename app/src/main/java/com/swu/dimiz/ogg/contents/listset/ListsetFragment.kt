package com.swu.dimiz.ogg.contents.listset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.chip.Chip
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentListsetBinding

class ListsetFragment : Fragment() {

    private var _binding : FragmentListsetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ListsetViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_listset, container, false)
        viewModel = ViewModelProvider(this).get(ListsetViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.activityList.observe(viewLifecycleOwner, object : Observer<List<String>> {
            override fun onChanged(value: List<String>) {
                value ?: return
                val chipGroup = binding.activityFilter
                val inflater = LayoutInflater.from(chipGroup.context)

                val children = value.map { category ->
                    val chip = inflater.inflate(R.layout.item_chips, chipGroup, false) as Chip
                    chip.text = category
                    chip.tag = category
                    chip.setOnCheckedChangeListener { button, isChecked ->
                        viewModel.onFilterChanged(button.tag as String, isChecked)
                    }
                    chip
                }
                chipGroup.removeAllViews()

                for(chip in children) {
                    chipGroup.addView(chip)
                }
            }
        })

        binding.upButton.setOnClickListener { view: View ->
            view.findNavController().navigateUp()
        }

        binding.buttonAllset.setOnClickListener { view: View ->

            // viewModel 문제 해결 필요
            view.findNavController().navigate(
                R.id.navigation_env)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}