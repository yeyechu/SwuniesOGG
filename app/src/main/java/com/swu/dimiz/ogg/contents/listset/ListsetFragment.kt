package com.swu.dimiz.ogg.contents.listset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentListsetBinding
import com.swu.dimiz.ogg.ui.env.badges.BadgeListFragmentDirections

class ListsetFragment : Fragment() {

    private lateinit var binding : FragmentListsetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_listset, container, false)

        binding.upButton.setOnClickListener { view: View ->
            view.findNavController().navigate(
                R.id.navigation_env
            )
        }

        return binding.root
    }
}