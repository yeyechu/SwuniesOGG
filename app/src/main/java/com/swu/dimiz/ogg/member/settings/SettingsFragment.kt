package com.swu.dimiz.ogg.member.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.MainActivity
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentSettingsBinding
import com.swu.dimiz.ogg.member.login.SignInActivity
import com.swu.dimiz.ogg.ui.env.EnvViewModel
import com.swu.dimiz.ogg.ui.myact.uploader.CameraActivity
import timber.log.Timber

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private val viewModel: EnvViewModel by activityViewModels()

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_settings, container, false)

        val mainActivity = activity as MainActivity
        auth = Firebase.auth

        //signout_btn
        binding.logoutBtn.setOnClickListener {
            auth.signOut()
            MainActivity.mainActivity!!.finish()
        }

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationIcon(R.drawable.common_button_arrow_left_svg)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }

}