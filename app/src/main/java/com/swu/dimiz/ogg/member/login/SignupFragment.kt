package com.swu.dimiz.ogg.member.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.databinding.FragmentSignupBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyBadge
import kotlinx.coroutines.launch
import timber.log.Timber



class SignupFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater, com.swu.dimiz.ogg.R.layout.fragment_signup, container, false
        )

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                    InputField
        binding.sendEmailBtn.setOnClickListener {
            val nickname = binding.nicknameEt.editText?.text.toString()
            val email = binding.emailEt.editText?.text.toString()
            val password1 = binding.passwordEtFirst.editText?.text.toString()
            val password2 = binding.passwordEtSecond.editText?.text.toString()
            signup(email, password1, nickname)


            if (password1 != password2) {
                binding.passwordEtSecond.error = "비밀번호가 일치하지 않습니다."
                return@setOnClickListener
            }
        }

        binding.passwordEtFirst.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { } //작성 전
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 작성 중
                val password1 = s.toString()
                if (password1.length < 8) {
                    binding.passwordEtFirst.error = "비밀번호를 8자 이상 입력해주세요"
                    //인증보내기 버튼 비활성화
                } else {
                    binding.passwordEtFirst.error = null
                }
            }
            override fun afterTextChanged(s: Editable?) { } //작성 후
        })

        binding.passwordEtSecond.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { } //작성 전
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 작성 중
                val password1 = binding.passwordEtFirst.editText?.text.toString()
                val password2 = s.toString()
                if (password2 != password1) {
                    binding.passwordEtSecond.error = "비밀번호가 일치하지 않아요"
                    //인증보내기 버튼 비활성화
                } else {
                    binding.passwordEtSecond.error = null
                }
            }
            override fun afterTextChanged(s: Editable?) { } //작성 후
        })

        //이메일 중복 확인 버튼-> 사용 가능한 이메일에요 // error:이미 가입되어있는 이메일이에요
        binding.emailCheck.setOnClickListener {
            val email = binding.emailEt.editText?.text.toString()

            // Firebase에서 해당 이메일이 이미 등록되어 있는지 확인
            auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val signInMethods = task.result?.signInMethods

                        if (signInMethods.isNullOrEmpty()) {
                            // 사용 가능한 이메일
                            binding.emailEt.error = null
                            binding.emailEt.helperText = "사용 가능한 이메일에요"
                        } else {
                            // 이미 가입된 이메일
                            binding.emailEt.error = "이미 가입되어있는 이메일이에요"
                            binding.emailEt.helperText = null
                            //인증보내기 버튼 비활성화
                        }
                    } else {
                        Timber.e(task.exception, "이메일 확인 중 오류 발생")
                    }
                }
        }


        return binding.root
    }

    // ──────────────────────────────────────────────────────────────────────────────────────
    //                                      회원가입 (파이어베이스)
    private fun signup(email: String, password: String, nickname: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.i("회원 가입 완료")

                    val user = FirebaseAuth.getInstance().currentUser

                    // 유저 닉네임 추가하기
                    val profileUpdates = userProfileChangeRequest {
                        displayName = nickname
                    }
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Timber.i("프로필 업데이트 완료")
                            }
                        }

                    // 인증메일 보내기
                    user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            Timber.i("확인메일을 보냈습니다")
                        } else {
                            Timber.i(verificationTask.exception.toString())
                        }
                    }

                    val fireDB = Firebase.firestore
                    for (i in 1..31) {
                        val badge = MyBadge(badgeID = 40000 + i)
                        fireDB.collection("User").document(email)
                            .collection("Badge").document((40000 + i).toString())
                            .set(badge)
                            .addOnSuccessListener { }
                            .addOnFailureListener { e -> Timber.i(e) }
                    }

                    view?.findNavController()
                        ?.navigate(com.swu.dimiz.ogg.R.id.action_signupFragment_to_signinFragment)

                } else if (task.exception?.message.isNullOrEmpty()) {
                    Timber.i(task.exception.toString())
                } else {
                    // 입력한 계정 정보가 이미 Firebase DB에 있는 경우
                    Timber.i("이미 존재하는 이메일입니다.")
                }
            }
    }

    private fun fireBadgeReset(){

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}
