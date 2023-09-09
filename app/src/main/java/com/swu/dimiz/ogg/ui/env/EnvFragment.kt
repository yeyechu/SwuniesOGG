package com.swu.dimiz.ogg.ui.env

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentEnvBinding

class EnvFragment : Fragment() {

    private lateinit var binding: FragmentEnvBinding
    private lateinit var startButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_env, container, false)

        startButton = binding.root.findViewById(R.id.env_button_start)

        binding.badgeEditButton.setOnClickListener { view: View ->
            view.findNavController().navigate(
                EnvFragmentDirections
                    .actionNavigationEnvToDestinationMyenv()
            )
        }
        startButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.destination_listset)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val envToolbar = binding.envToolbar
        envToolbar.inflateMenu(R.menu.env_menu)

        envToolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_badges -> {
                    view.findNavController().navigate(
                        EnvFragmentDirections
                            .actionNavigationEnvToDestinationBadgeList()
                    )
                    true
                }
                R.id.action_my_page -> {
                    view.findNavController().navigate(
                        EnvFragmentDirections
                            .actionNavigationEnvToDestinationMember()
                    )
                    true
                }
                else -> false
            }
        }
    }
}