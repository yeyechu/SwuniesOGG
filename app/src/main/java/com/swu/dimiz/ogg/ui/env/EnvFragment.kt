package com.swu.dimiz.ogg.ui.env

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentEnvBinding
import timber.log.Timber

class EnvFragment : Fragment() {

    private var _binding: FragmentEnvBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EnvViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                    기본 초기화
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_env, container, false)

        navController = findNavController()
        binding.viewModel = viewModel

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                               환경 수정 플로팅 버튼 정의

        binding.badgeEditButton.apply {
            setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
            foreground = ContextCompat.getDrawable(requireContext(), R.drawable.env_button_edit_badge_floating)
        }

        viewModel.navigateToMyEnv.observe(viewLifecycleOwner, Observer<Boolean> { shouldNavigate ->
            if(shouldNavigate) {
                navController.navigate(
                    EnvFragmentDirections.actionNavigationEnvToDestinationMyenv())
                viewModel.onNavigatedMyEnv()
            }
        })
        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                프로젝트 시작 버튼 정의
        viewModel.navigateToStart.observe(viewLifecycleOwner, Observer<Boolean> { shouldNavigate ->
            if(shouldNavigate) {
                navController.navigate(
                    EnvFragmentDirections.actionNavigationEnvToDestinationListaim())
                viewModel.onNavigatedToStart()
            }
        })
        return binding.root
    }

    // ──────────────────────────────────────────────────────────────────────────────────────
    //                                         툴바 초기화
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val envToolbar = binding.envToolbar
        envToolbar.inflateMenu(R.menu.env_menu)

        envToolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_badges -> {
                    navController.navigate(
                        EnvFragmentDirections
                            .actionNavigationEnvToDestinationBadgeList()
                    )
                    true
                }
                R.id.action_my_page -> {
                    navController.navigate(
                        EnvFragmentDirections
                            .actionNavigationEnvToDestinationMember()
                    )
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}