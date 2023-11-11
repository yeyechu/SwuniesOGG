package com.swu.dimiz.ogg.member

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
import com.swu.dimiz.ogg.databinding.FragmentMemberBinding
import com.swu.dimiz.ogg.ui.env.EnvViewModel
import com.swu.dimiz.ogg.ui.feed.FeedViewModel
import com.swu.dimiz.ogg.ui.feed.myfeed.MyFeedDetailWindow
import timber.log.Timber

class MemberFragment : Fragment() {

    private var _binding: FragmentMemberBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var fragmentManager: FragmentManager

    private val viewModel: EnvViewModel by activityViewModels()
    private val feedViewModel: FeedViewModel by activityViewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_member, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        navController.setLifecycleOwner(viewLifecycleOwner)

        fragmentManager = childFragmentManager

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationIcon(R.drawable.common_button_arrow_left_svg)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.buttonSettings.setOnClickListener {
            it?.let {
                navController.navigate(R.id.destination_settings)
            }
        }

        feedViewModel.navigateToSelectedItem.observe(viewLifecycleOwner) {
            it?.let {
                addDetailWindow()
            }
        }
    }

    private fun addDetailWindow() {
        fragmentManager.beginTransaction()
            .add(R.id.frame_layout_member, MyFeedDetailWindow())
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