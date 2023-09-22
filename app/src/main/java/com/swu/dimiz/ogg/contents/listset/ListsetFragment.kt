package com.swu.dimiz.ogg.contents.listset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.chip.Chip
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentListsetBinding
import com.swu.dimiz.ogg.oggdata.OggRepository
import timber.log.Timber

class ListsetFragment : Fragment() {

    private var _binding : FragmentListsetBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ListsetViewModel
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                    기본 초기화

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_listset, container, false)

        navController = findNavController()

        val application = requireNotNull(this.activity).application
        val viewModelFactory = ListsetViewModelFactory((application as OggApplication).repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(ListsetViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                               활동 목표 출력 및 초기화

        val listsetFragmentArgs by navArgs<ListsetFragmentArgs>()
        binding.textAim.text = listsetFragmentArgs.aimCo2Amount.toString()

        viewModel.co2Aim.observe(viewLifecycleOwner, Observer<Float> {
            viewModel.setCo2(listsetFragmentArgs.aimCo2Amount)
        })

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                     카테고리 출력

        viewModel.activityFilter.observe(viewLifecycleOwner, object : Observer<List<String>> {

            override fun onChanged(value: List<String>) {
                value ?: return
                val chipGroup = binding.activityFilter
                val inflater = LayoutInflater.from(chipGroup.context)

                val children = value.map { category ->
                    val chip = inflater.inflate(R.layout.item_chips, chipGroup, false) as Chip
                    chip.text = category
                    chip.tag = category
                    if(category == "energy") {
                        chip.isChecked = true
                    }
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

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       어댑터
        val adapter = ActivityListAdapter(requireContext())
        binding.activityList.adapter = adapter

        viewModel.filteredList.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                     완료 버튼을 누르면 저장 후 화면을 이동시키는 관찰자

        viewModel.navigateToSave.observe(viewLifecycleOwner, Observer { shouldNavigate ->
            if(shouldNavigate) {
                navController.navigate(R.id.navigation_env)
                viewModel.onNavigatedToSave()
                Timber.i("완료 버튼 클릭")
                // 메모리 누수 확인 필요
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}