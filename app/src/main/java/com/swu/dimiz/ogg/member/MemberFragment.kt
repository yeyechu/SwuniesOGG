package com.swu.dimiz.ogg.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentMemberBinding
import com.swu.dimiz.ogg.ui.env.badges.BadgeListFragmentDirections

class MemberFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentMemberBinding
        = DataBindingUtil.inflate(
            inflater, R.layout.fragment_member, container, false)

        binding.upButton.setOnClickListener { view: View ->
            view.findNavController().navigateUp()
        }

        binding.buttonSettings.setOnClickListener { view: View ->
            view.findNavController().navigate(
                R.id.destination_settings
            )
        }
        return binding.root
    }

}