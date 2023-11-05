package com.swu.dimiz.ogg.ui.graph

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class GraphFragmentStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragments = mutableListOf<Fragment>()

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }


    fun setFragments(fragments: List<Fragment>) {
        this.fragments.clear()
        this.fragments.addAll(fragments)
        notifyDataSetChanged()
    }
}

