package com.swu.dimiz.ogg.member.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.MainActivity
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentSettingsBinding
import com.swu.dimiz.ogg.member.login.SignInActivity
import timber.log.Timber

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
//    private val viewModel: EnvViewModel by activityViewModels()

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_settings, container, false)

        auth = Firebase.auth

        binding.changeNicknameBtn.setOnClickListener {

            it?.let {
                navController.navigate(R.id.destination_setting_nickname)
            }
        }

        binding.changePasswordBtn.setOnClickListener {

            it?.let {
                navController.navigate(R.id.destination_setting_password)
            }
        }

        binding.mycarBtn.setOnClickListener {

            it?.let {
                navController.navigate(R.id.destination_setting_car)
            }
        }
        binding.signoutBtn.setOnClickListener {

            it?.let {
                navController.navigate(R.id.destination_setting_signout)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        navController.setLifecycleOwner(viewLifecycleOwner)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationIcon(R.drawable.common_button_arrow_left_svg)

        binding.logoutBtn.setOnClickListener {
            val intent = Intent(context, SignInActivity::class.java)
            auth.signOut()
            requireContext().startActivity(intent)
            MainActivity.mainActivity!!.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }

}