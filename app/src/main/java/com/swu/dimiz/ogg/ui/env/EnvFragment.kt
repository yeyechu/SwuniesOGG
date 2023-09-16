package com.swu.dimiz.ogg.ui.env

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentEnvBinding
import timber.log.Timber

class EnvFragment : Fragment() {

    private var _binding: FragmentEnvBinding? = null
    private val binding get() = _binding!!

    private lateinit var startButton: Button
    private lateinit var viewModel: EnvViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_env, container, false)

        viewModel = ViewModelProvider(this).get(EnvViewModel::class.java)
        binding.viewModel = viewModel

        //                      환경 수정 플로팅 버튼 정의
        binding.badgeEditButton.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.white))
        binding.badgeEditButton.foreground =
            ContextCompat.getDrawable(
            requireContext(), R.drawable.env_button_edit_badge_floating)

        viewModel.navigate.observe(viewLifecycleOwner, Observer<Boolean> { shouldNavigate ->
            if(shouldNavigate) {
                val navController = findNavController()
                navController.navigate(
                    EnvFragmentDirections.actionNavigationEnvToDestinationMyenv())
                viewModel.onNavigated()
            }
        })

        //                      프로젝트 시작 버튼 정의
        startButton = binding.root.findViewById(R.id.env_button_start)
        startButton.setOnClickListener {
            viewModel.onStartClicked()
        }

        viewModel.navigateToStart.observe(viewLifecycleOwner, Observer<Boolean> { shouldNavigate ->
            if(shouldNavigate) {
                val navController = findNavController()
                navController.navigate(
                    EnvFragmentDirections.actionNavigationEnvToDestinationListaim())
                viewModel.onNavigatedToStart()
            }

        })
//        startButton.setOnClickListener { view: View ->
//            view.findNavController().navigate(
//                EnvFragmentDirections.actionNavigationEnvToDestinationListaim())
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //                      툴바 정의
        Timber.i("onViewCreated()")
        val envToolbar = binding.envToolbar
        envToolbar.inflateMenu(R.menu.env_menu)

        envToolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_badges -> {
                    view.findNavController().navigate(
                        EnvFragmentDirections
                            .actionNavigationEnvToDestinationBadgeList()
                    )
                    true
                }
                R.id.action_my_page -> {
                    view.findNavController().navigate(
                        EnvFragmentDirections
                            .actionNavigationEnvToDestinationMember()
                    )
                    true
                }
                else -> false
            }
        }
    }

    //                           수명 주기 확인
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.i("onAttach()")
    }

    override fun onStart() {
        super.onStart()
        Timber.i("onStart()")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume()")
    }

    override fun onPause() {
        super.onPause()
        Timber.i("onPause()")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStop()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDesrtoy()")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.i("onDetach()")
    }
}