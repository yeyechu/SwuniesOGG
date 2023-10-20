package com.swu.dimiz.ogg.member.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentSigninFindPasswordBinding
import timber.log.Timber

class FindPassword: Fragment() {
    private var _binding : FragmentSigninFindPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_signin_find_password, container, false)

        binding.sendEmailBtn.setOnClickListener(){
            val email =  binding.emailEt.editText?.text.toString()

            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.i("Email sent.")
                        Toast.makeText(activity,"이메일로 비밀번호 재설정 링크 보냈어요", Toast.LENGTH_SHORT).show()
                        //todo 화면 이동
                    }
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