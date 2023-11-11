package com.swu.dimiz.ogg.member.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.OggSnackbar
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentSignupBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyBadge
import timber.log.Timber

class SignupFragment : Fragment() {

    private lateinit var navController: NavController

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_signup, container, false
        )

        // ──────────────────────────────────────────────────────────────────────────────────────
        //                                    InputField
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
            OggApplication.auth.fetchSignInMethodsForEmail(email)
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
                        binding.emailEt.error = "올바른 이메일 형식으로 입력해주세요"

                    }
                }
        }
        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            // 체크박스가 체크되었을 때
            binding.sendEmailBtn.isEnabled = isChecked
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        navController.setLifecycleOwner(viewLifecycleOwner)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationIcon(R.drawable.common_button_arrow_left_svg)

        binding.sendEmailBtn.setOnClickListener {
            val nickname = binding.nicknameEt.editText?.text.toString()
            val email = binding.emailEt.editText?.text.toString()
            val password1 = binding.passwordEtFirst.editText?.text.toString()
            val password2 = binding.passwordEtSecond.editText?.text.toString()
            signup(email, password1, nickname)

            navController.navigateUp()
            OggSnackbar.make(requireView(), "이메일로 인증을 완료해주세요.").show()

            if (password1 != password2) {
                binding.passwordEtSecond.error = "비밀번호가 일치하지 않습니다."
                return@setOnClickListener
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────────────
    //                                      회원가입 (파이어베이스)
    private fun signup(email: String, password: String, nickname: String) {
        OggApplication.auth.createUserWithEmailAndPassword(email, password)
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

                } else if (task.exception?.message.isNullOrEmpty()) {
                    Timber.i(task.exception.toString())
                } else {
                    // 입력한 계정 정보가 이미 Firebase DB에 있는 경우
                    Timber.i("이미 존재하는 이메일입니다.")
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}
