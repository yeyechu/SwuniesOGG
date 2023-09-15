package com.swu.dimiz.ogg.member.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentSettingsBinding
                = DataBindingUtil.inflate(
            inflater, R.layout.fragment_settings, container, false)

        binding.upButton.setOnClickListener { view: View ->
            view.findNavController().navigateUp()
        }

        return binding.root
    }

}