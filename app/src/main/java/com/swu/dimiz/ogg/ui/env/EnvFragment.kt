package com.swu.dimiz.ogg.ui.env

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentEnvBinding
import timber.log.Timber

class EnvFragment : Fragment() {

    private var _binding: FragmentEnvBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EnvViewModel by activityViewModels()
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_env, container, false)

        viewModel.fireInfo()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        Timber.i("프래그먼트 onViewCreated()")

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                               환경 수정 플로팅 버튼 정의

        binding.badgeEditButton.apply {
            setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
            foreground = ContextCompat.getDrawable(requireContext(), R.drawable.env_button_edit_badge_floating)
        }

        viewModel.navigateToMyEnv.observe(viewLifecycleOwner) {
            if(it) {
                navController.navigate(
                    EnvFragmentDirections.actionNavigationEnvToDestinationMyenv())
                viewModel.onNavigatedMyEnv()
            }
        }
        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                프로젝트 시작 버튼 정의
        viewModel.navigateToStart.observe(viewLifecycleOwner) {
            if(it) {
                navController.navigate(
                    EnvFragmentDirections.actionNavigationEnvToDestinationListaim())
                viewModel.onNavigatedToStart()
            }
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                            오늘 날짜, 스탬프 정보 업데이트
        viewModel.todayCo2.observe(viewLifecycleOwner) {
           viewModel.updateTodayStampToFirebase()
        }

        viewModel.co2Holder.observe(viewLifecycleOwner) {
            viewModel.leftCo2()
        }

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                  툴바 이동 정의
        val envToolbar = binding.envToolbar
        envToolbar.inflateMenu(R.menu.env_menu)

        envToolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_badges -> {
                    navController.navigate(
                        EnvFragmentDirections.actionNavigationEnvToDestinationBadgeList()
                    )
                    true
                }
                R.id.action_my_page -> {
                    navController.navigate(
                        EnvFragmentDirections.actionNavigationEnvToDestinationMember()
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