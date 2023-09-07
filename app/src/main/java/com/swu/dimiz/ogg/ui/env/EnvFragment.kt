package com.swu.dimiz.ogg.ui.env

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentEnvBinding

class EnvFragment : Fragment() {

    private lateinit var binding: FragmentEnvBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_env, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val envToolbar = binding.envToolbar
        envToolbar.inflateMenu(R.menu.env_menu)

        envToolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_badges -> {

                    true
                }
                R.id.action_my_page -> {
                    true
                }
                else -> false
            }
        }
    }
}