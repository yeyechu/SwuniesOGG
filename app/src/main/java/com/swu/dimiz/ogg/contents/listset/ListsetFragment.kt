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
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.chip.Chip
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentListsetBinding
import com.swu.dimiz.ogg.ui.env.EnvViewModel
import timber.log.Timber

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


        val application = requireNotNull(this.activity).application
        val arguments = ListsetFragmentArgs.fromBundle(requireArguments())

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

        binding.buttonAllset.setOnClickListener { view: View ->

            Timber.i("완료 버튼 클릭")
            // viewModel 문제 해결 필요
            view.findNavController().navigate(
                R.id.navigation_env)
            // 메모리 누수 확인 필요
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}