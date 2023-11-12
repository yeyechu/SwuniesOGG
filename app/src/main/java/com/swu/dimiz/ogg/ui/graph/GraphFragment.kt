package com.swu.dimiz.ogg.ui.graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentGraphBinding
import timber.log.Timber

class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GraphViewModel by activityViewModels { GraphViewModel.Factory }
    private lateinit var navController: NavController

    private lateinit var viewPager2: ViewPager2
    private lateinit var viewpager2Adapter: GraphFragmentStateAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_graph, container, false
        )
        viewModel.fireInfo()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        navController.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewPager2 = binding.viewpagerGraph
        viewpager2Adapter = GraphFragmentStateAdapter(this)
        viewPager2.adapter = viewpager2Adapter

        viewModel.userCondition.observe(viewLifecycleOwner) {
            if(it.email != "") {
                viewpager2Adapter.pagerSize = it.projectCount
                viewModel.fireGetCategory(it.projectCount)
                viewModel.fireGetCo2(it.projectCount)
                viewModel.fireGetMostPost(it.projectCount)
                viewModel.fireGetExtra(it.projectCount)
                viewModel.fireGetReaction(it.projectCount)
            }
        }

        viewModel.leftPager.observe(viewLifecycleOwner) {
            if(it) {
                val currentPage = viewPager2.currentItem

                viewPager2.setCurrentItem(currentPage - 1, true)
                viewModel.onLeftCompleted()
                viewModel.setCurrentPage(currentPage)
            }
        }

        viewModel.rightPager.observe(viewLifecycleOwner) {
            if(it) {
                val currentPage = viewPager2.currentItem

                viewPager2.setCurrentItem(currentPage + 1, true)
                viewModel.onRightCompleted()
               viewModel.setCurrentPage(currentPage)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}