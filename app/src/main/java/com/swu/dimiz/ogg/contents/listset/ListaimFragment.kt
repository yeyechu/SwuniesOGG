package com.swu.dimiz.ogg.contents.listset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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
                viewModel.onUpButtonClicked()
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

        viewModel.aimCo2.observe(viewLifecycleOwner) {
            viewModel.setAimTitle(it)
        }

        viewModel.navigateToSelection.observe(viewLifecycleOwner) {
            if(it) {
                navController.navigate(
                    ListaimFragmentDirections.actionDestinationListaimToDestinationListset()
                )
                viewModel.onNavigatedToSelection()
            }
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                      팝업버튼 정의
        binding.includedLayoutDialogExit.buttonCancel.setOnClickListener {
            viewModel.onNavigatedToDialog()
        }

        binding.includedLayoutDialogExit.buttonExit.setOnClickListener {
            viewModel.onNavigatedToDialog()
            viewModel.resetFrequency()
            navController.navigateUp()
            viewModel.initListHolder()
        }

        binding.includedLayoutDialogExit.dialogLayout.setOnClickListener {
            viewModel.noClick()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}