package com.swu.dimiz.ogg.ui.env

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.swu.dimiz.ogg.ui.env.stamp.StampViewModel
import timber.log.Timber

class EnvFragment : Fragment() {

    private var _binding: FragmentEnvBinding? = null
    private val binding get() = _binding!!

    private lateinit var startButton: Button
    private lateinit var expandButton: ImageButton
    private lateinit var expandLayout: ConstraintLayout

    private lateinit var beforeLayout: LinearLayout
    private lateinit var afterLayout: ConstraintLayout

    private lateinit var viewModel: EnvViewModel
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                    기본 초기화

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_env, container, false)

        navController = findNavController()

        viewModel = ViewModelProvider(this).get(EnvViewModel::class.java)
        binding.viewModel = viewModel

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                               환경 수정 플로팅 버튼 정의
        binding.badgeEditButton.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.white))
        binding.badgeEditButton.foreground =
            ContextCompat.getDrawable(
            requireContext(), R.drawable.env_button_edit_badge_floating)

        viewModel.navigate.observe(viewLifecycleOwner, Observer<Boolean> { shouldNavigate ->
            if(shouldNavigate) {
                navController.navigate(
                    EnvFragmentDirections.actionNavigationEnvToDestinationMyenv())
                viewModel.onNavigated()
            }
        })

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                프로젝트 시작 버튼 정의
        startButton = binding.root.findViewById(R.id.env_button_start)
        startButton.setOnClickListener {
            viewModel.onStartClicked()
        }

        viewModel.navigateToStart.observe(viewLifecycleOwner, Observer<Boolean> { shouldNavigate ->
            if(shouldNavigate) {
                navController.navigate(
                    EnvFragmentDirections.actionNavigationEnvToDestinationListaim())
                viewModel.onNavigatedToStart()
            }
        })

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                스탬프 레이아웃

        expandButton = binding.root.findViewById(R.id.button_expand)
        expandLayout = binding.root.findViewById(R.id.layout_stamp)

        beforeLayout = binding.root.findViewById(R.id.before_layout)
        afterLayout = binding.root.findViewById(R.id.after_layer)

        expandButton.setOnClickListener {
            viewModel.onExpandButtonClicked()
        }

        viewModel.expandLayout.observe(viewLifecycleOwner) {
            if(it) {
                expandLayout.visibility = View.VISIBLE
                expandButton.setImageResource(R.drawable.common_button_arrow_up)
            } else {
                expandLayout.visibility = View.GONE
                expandButton.setImageResource(R.drawable.common_button_arrow_down)
            }
        }

        return binding.root
    }

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


        viewModel.date.observe(viewLifecycleOwner) {
            if (it == 0) {
                beforeLayout.visibility = View.VISIBLE
                afterLayout.visibility = View.GONE
            } else {
                afterLayout.visibility = View.VISIBLE
                beforeLayout.visibility = View.GONE
            }
        }
    }
}