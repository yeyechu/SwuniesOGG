package com.swu.dimiz.ogg.ui.graph

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.swu.dimiz.ogg.contents.listset.listutils.GRAPH_OBJECT

class GraphFragmentStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    var pagerSize: Int = 0

    override fun getItemCount(): Int = pagerSize

    override fun createFragment(position: Int): Fragment {
        val fragment = GraphLayer()
        fragment.arguments = Bundle().apply {
            putInt(GRAPH_OBJECT, position)
        }
        return fragment
    }
}

