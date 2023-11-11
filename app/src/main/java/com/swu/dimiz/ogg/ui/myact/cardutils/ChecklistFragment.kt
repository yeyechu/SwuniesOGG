package com.swu.dimiz.ogg.ui.myact.cardutils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentChecklistBinding
import com.swu.dimiz.ogg.ui.myact.MyActViewModel
import timber.log.Timber

class ChecklistFragment : Fragment() {

    private var _binding: FragmentChecklistBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private val viewModel: MyActViewModel by activityViewModels { MyActViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChecklistBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        navController.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationIcon(R.drawable.common_button_arrow_left_svg)

        val recyclerView = binding.mRecyclerView
        val adapter = ChecklistAdapter(ChecklistAdapter.ChecklistClickListener {
            // 체크 리스너 구현
        })

        // 리사이클러뷰에 레이아웃 관리자 연결
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.postChecklistId.observe(viewLifecycleOwner) {
            it?.let {
                when(it) {
                    10011 -> adapter.submitList(adapter.drivingList)
                    20007 -> adapter.submitList(adapter.tireList)
                }
            }
        }
        viewModel.postToChecklist.observe(viewLifecycleOwner) {
            if(it) {
                val date = System.currentTimeMillis()

                when (viewModel.postChecklistId.value) {
                    10011 -> {
                        viewModel.updateDailyPostCount()
                        viewModel.fireUpdateAll(date)
                        viewModel.fireUpdateBadgeDate(date)
                        viewModel.updateBadgeDateCo2(date)
                    }
                    20007 -> {
                        // 타이어 휠 점검에 대한 업데이트 구현
                    }
                }
                viewModel.onChecklistPosted()
                navController.navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}