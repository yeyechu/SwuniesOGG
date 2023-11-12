package com.swu.dimiz.ogg.member.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.swu.dimiz.ogg.OggSnackbar
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

        binding.presentPasswordBtn.setOnClickListener {
            val presentPwd = binding.presentPassword.editText?.text.toString()

            //계정 삭제, 기본 이메일 주소 설정, 비밀번호 변경과 같이 보안에 민감한 작업을 하려면 사용자가 최근에 로그인한 적이 있어야 함
            val credential = EmailAuthProvider
                .getCredential( fireUser?.email.toString(), presentPwd)

            fireUser?.reauthenticate(credential)?.addOnCompleteListener {
                Timber.i("User re-authenticated.")
                binding.presentPassword.helperText = "현재 비밀번호가 확인되었어요"
            }
                ?.addOnFailureListener {
                    binding.presentPassword.error = "올바른 비밀 번호를 입력해주세요"
                }
        }

        binding.newPasswordEt.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { } //작성 전
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 작성 중
                val password1 = s.toString()
                if (password1.length < 8) {
                    binding.newPasswordEt.error = "비밀번호를 8자 이상 입력해주세요"
                    //인증보내기 버튼 비활성화
                } else {
                    binding.newPasswordEt.error = null
                }
            }
            override fun afterTextChanged(s: Editable?) { } //작성 후
        })

//        binding.newPasswordAgainEt.editText?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { } //작성 전
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                // 작성 중
//                val password1 = binding.newPasswordAgainEt.editText?.text.toString()
//                val password2 = s.toString()
//                if (password2 != password1) {
//                    binding.newPasswordAgainEt.error = "비밀번호가 일치하지 않아요"
//                    //인증보내기 버튼 비활성화
//                } else {
//                    binding.newPasswordAgainEt.error = null
//                }
//            }
//            override fun afterTextChanged(s: Editable?) { } //작성 후
//        })
        binding.newPasswordAgainEt.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { } // 작성 전

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 작성 중
                val password1 = binding.newPasswordEt.editText?.text.toString() // 이전에 입력한 비밀번호
                val password2 = s.toString() // 확인용 비밀번호

                if (password2 != password1) {
                    binding.newPasswordAgainEt.error = "비밀번호가 일치하지 않아요"
                    // 인증보내기 버튼 비활성화
                    binding.passwordBtn.isEnabled = false
                } else {
                    binding.newPasswordAgainEt.error = null
                    // 인증보내기 버튼 활성화
                    binding.passwordBtn.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) { } // 작성 후
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationIcon(R.drawable.common_button_arrow_left_svg)

        binding.passwordBtn.setOnClickListener{
            val newPassword = binding.newPasswordEt.editText?.text.toString()

            //비밀번호 업데이트
            fireUser!!.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.i("User password updated.")
                    }
                }

            navController.navigateUp()
            OggSnackbar.make(view, getText(R.string.setting_toast_password_changed).toString()).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }


}