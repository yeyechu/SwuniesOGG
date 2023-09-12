package com.swu.dimiz.ogg.contents.listset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentListaimBinding

class ListaimFragment  : Fragment() {

    private lateinit var binding : FragmentListaimBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_listaim, container, false)

        binding.upButton.setOnClickListener { view: View ->
            view.findNavController().navigate(
                ListaimFragmentDirections
                    .actionDestinationListaimToNavigationEnv()
            )
        }

        binding.buttonSelection.setOnClickListener { view: View ->
            view.findNavController().navigate(
                ListaimFragmentDirections
                    .actionDestinationListaimToDestinationListset())
        }

        return binding.root
    }
}