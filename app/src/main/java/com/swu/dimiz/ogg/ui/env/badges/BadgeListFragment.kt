package com.swu.dimiz.ogg.ui.env.badges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentBadgeListBinding
import timber.log.Timber

class BadgeListFragment : Fragment() {

    private var _binding: FragmentBadgeListBinding? = null
    private val binding get() = _binding!!
    private lateinit var fragmentManager: FragmentManager

    private lateinit var viewModel: BadgeListViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                  화면 및 뷰모델 초기화
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_badge_list, container, false)

        navController = findNavController()
        fragmentManager = childFragmentManager

        val application = requireNotNull(this.activity).application
        val viewModelFactory = BadgeListViewModelFactory((application as OggApplication).repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(BadgeListViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       어댑터

        val headerAdapter = HeaderAdapter()
        binding.badgeList.adapter = BadgeListAdapter(BadgeListAdapter.BadgeClickListener {
            viewModel.showPopup(it)
        })
        val concatAdapter = ConcatAdapter(headerAdapter, binding.badgeList.adapter)

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                       어댑터

        viewModel.navigateToSelected.observe(viewLifecycleOwner) {
            it?.let {
                addWindow()

                viewModel.completedPopup()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun addWindow() {
        fragmentManager.beginTransaction()
            .add(R.id.frame_layout, BadgeDetailWindow())
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}