package com.swu.dimiz.ogg.ui.graph

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class GraphFragmentStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    var pagerSize: Int = 0

    override fun getItemCount(): Int = pagerSize

    override fun createFragment(position: Int): Fragment = GraphLayer.create(position)
}

