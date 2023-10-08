package com.swu.dimiz.ogg.contents.listset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.listutils.ListHolderAdapter
import com.swu.dimiz.ogg.databinding.FragmentListsetBinding
import timber.log.Timber

class ListsetFragment : Fragment() {

    private var _binding : FragmentListsetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListsetViewModel by activityViewModels { ListsetViewModel.Factory }
    private lateinit var navController: NavController
    private lateinit var fragmentManager: FragmentManager

    private lateinit var listHolderAdapter: ListHolderAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_listset, container, false)

        val listView: GridView = binding.listHolder
        //val numberView: TextView = binding.root.findViewById(R.id.text_number)

        //사용자 정보 가져오기
        viewModel.fireInfo()
        //지속활동 진행중 리스트
        viewModel.fireGetSust()
        viewModel.fireGetExtra()
        viewModel.fireGetDaily()

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                      버튼 인터랙션
        viewModel.progressBar.observe(viewLifecycleOwner) {
            binding.progressBar.progress = it
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner) {
            it?.let {
                addWindow()
                viewModel.completedPopup()
            }
        }

        viewModel.listHolder.observe(viewLifecycleOwner) {
            it?.let {
                listHolderAdapter = ListHolderAdapter(requireContext(), it)
                listView.adapter = listHolderAdapter
            }
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                     완료 버튼을 누르면 저장 후 화면을 이동시키는 관찰자
        viewModel.navigateToSave.observe(viewLifecycleOwner) {
            if(it) {
                navController.navigate(R.id.navigation_env)
                viewModel.onNavigatedToSave()
                Timber.i("완료 버튼 클릭")
                // 메모리 누수 확인 필요

                viewModel.fireSave()

            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        fragmentManager = childFragmentManager

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        //binding.toolbar.setNavigationIcon(R.drawable.common_button_arrow_left)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun addWindow() {
        fragmentManager.beginTransaction()
            .add(R.id.frame_layout_listset, ListDetailWindow())
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