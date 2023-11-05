package com.swu.dimiz.ogg.ui.graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentGraphBinding
import timber.log.Timber

class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!

    private val fragmentViewModel: GraphViewModel by activityViewModels { GraphViewModel.Factory }
    private lateinit var navController: NavController

    private var projcnt = 1

    private lateinit var viewPager2: ViewPager2
    private lateinit var viewpager2Adapter: GraphFragmentStateAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_graph, container, false
        )

        viewPager2 = binding.viewpagerGraph
        viewpager2Adapter = GraphFragmentStateAdapter(this)
        viewPager2.adapter = viewpager2Adapter



        // 프래그먼트 목록을 설정
        val fragments = mutableListOf<Fragment>()
        for (i in 0 until 21) {
            fragments.add(GraphLayer())
        }
        viewpager2Adapter.setFragments(fragments)

        val textGraphTitle = binding.textGraphTitle
        textGraphTitle.text = "$projcnt 회"

        binding.buttonLeft.setOnClickListener {
            val currentCount = fragmentViewModel.getProjectCountLiveData().value ?: 0
            if (currentCount > 1) {
                val newCount = currentCount - 1
                fragmentViewModel.fetchFirebaseData(newCount)
            }
            handleButtonClick(-1)
            if (projcnt > 1) {
                projcnt -= 1
                updateText()
            }
        }

        binding.buttonRight.setOnClickListener {
            val currentCount = fragmentViewModel.getProjectCountLiveData().value ?: 0
            if (currentCount < 21) {
                val newCount = currentCount + 1
                fragmentViewModel.fetchFirebaseData(newCount)
            }
            handleButtonClick(1)
            if (projcnt < 21) {
                projcnt += 1
                updateText()
            }
        }


        // projectCount 데이터가 변경될 때 UI 업데이트
        fragmentViewModel.getProjectCountLiveData().observe(viewLifecycleOwner, Observer { count ->
            updateText()
            updateFragmentData(count)
        })

        return binding.root
    }
    private fun handleButtonClick(offset: Int) {
        val currentCount = fragmentViewModel.getProjectCountLiveData().value ?: 1

        val newCount = currentCount + offset

        if (newCount in 1..21) {
            fetchFirebaseData(newCount)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        navController.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = fragmentViewModel
        binding.lifecycleOwner = viewLifecycleOwner



    }


    private fun fetchFirebaseData(projectCount: Int) {
        fragmentViewModel.fetchFirebaseData(projectCount)
    }

    private fun updateText() {
        val textGraphTitle = binding.textGraphTitle
        textGraphTitle.text = "$projcnt 회"
    }

    private fun updateFragmentData(projcnt: Int) {
        val fragments = mutableListOf<Fragment>()
        for (i in 0 until projcnt) {
            fragments.add(GraphLayer())
        }
        viewpager2Adapter.setFragments(fragments)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}