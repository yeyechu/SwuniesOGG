package com.swu.dimiz.ogg.contents.listset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentListaimBinding
import timber.log.Timber

class ListaimFragment : Fragment() {

    private var _binding: FragmentListaimBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListsetViewModel by activityViewModels { ListsetViewModel.Factory }
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_listaim, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        navController.setLifecycleOwner(viewLifecycleOwner)

        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolbar.apply {
            setupWithNavController(navController, appBarConfiguration)
            setNavigationIcon(R.drawable.common_button_exit_toolbar)
            setPadding(20, 10, 20, 10)
            setNavigationOnClickListener {

            }
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.fireInfo()
        viewModel.userCondition.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.initCondition()
            }
        }
        // ──────────────────────────────────────────────────────────────────────────────────────
        //                       활동 목표 선택 버튼 누르면 내용을 변경하는 관찰자

        viewModel.aimCo2.observe(viewLifecycleOwner) {
            viewModel.setAimTitle(it)
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                          선택 버튼을 누르면 화면을 이동시키는 관찰자
        viewModel.navigateToSelection.observe(viewLifecycleOwner) {
            if(it) {
                navController.navigate(
                    ListaimFragmentDirections.actionDestinationListaimToDestinationListset()
                )
                viewModel.onNavigatedToSelection()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}