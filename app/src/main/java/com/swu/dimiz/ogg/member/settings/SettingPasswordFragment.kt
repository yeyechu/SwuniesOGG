package com.swu.dimiz.ogg.member.settings

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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentSettingPasswordBinding
import timber.log.Timber

class SettingPasswordFragment : Fragment() {

    private var _binding: FragmentSettingPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    val fireUser = Firebase.auth.currentUser
    val fireDB = Firebase.firestore


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_setting_password, container, false)



        binding.passwordBtn.setOnClickListener(){
            var newPassword = binding.newPasswordEt.editText?.text.toString()

            //계정 삭제, 기본 이메일 주소 설정, 비밀번호 변경과 같이 보안에 민감한 작업을 하려면 사용자가 최근에 로그인한 적이 있어야 함
            val credential = EmailAuthProvider
                .getCredential( fireUser?.email.toString(), "bmbm1234") //todo 이전 비번 값받아오기

            fireUser?.reauthenticate(credential)?.addOnCompleteListener { Timber.i("User re-authenticated.") }
                ?.addOnFailureListener {  } //비번 틀림

            //비밀번호 업데이트
            fireUser!!.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.i("User password updated.")
                    }
                }

            //화면 이동
            it?.let {
                navController.navigate(R.id.action_destination_setting_password_to_destination_settings)
            }
            Toast.makeText(activity,"비밀번호 변경이 완료되었어요!", Toast.LENGTH_SHORT).show();

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