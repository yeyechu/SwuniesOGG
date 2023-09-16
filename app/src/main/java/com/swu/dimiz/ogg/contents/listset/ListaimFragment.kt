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
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentListaimBinding
import timber.log.Timber

class ListaimFragment : Fragment() {

    private var _binding: FragmentListaimBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ListaimViewModel
    private lateinit var navController: NavController

    private var aimCo2Amount = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                    기본 초기화

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_listaim, container, false)

        navController = findNavController()

        viewModel = ViewModelProvider(this).get(ListaimViewModel::class.java)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.viewModel = viewModel


        // ──────────────────────────────────────────────────────────────────────────────────────
        //                       활동 목표 선택 버튼 누르면 내용을 변경하는 관찰자
        //                            변경될 내용들은 ViewModel에 정의

        viewModel.aimCo2.observe(viewLifecycleOwner) {
            aimCo2Amount = it
            viewModel.setAimTitle(aimCo2Amount)
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                          선택 버튼을 누르면 화면을 이동시키는 관찰자
        viewModel.navigateToSelection.observe(viewLifecycleOwner, Observer<Boolean> { shouldNavigate ->
            if(shouldNavigate) {
                navController.navigate(
                    ListaimFragmentDirections.actionDestinationListaimToDestinationListset(aimCo2Amount)
                )
                viewModel.onNavigatedToSelection()
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