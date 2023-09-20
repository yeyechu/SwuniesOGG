package com.swu.dimiz.ogg.member.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentSigninBinding
import com.swu.dimiz.ogg.databinding.FragmentSignupBinding
import timber.log.Timber

class SignupFragment : Fragment() {
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore

    private var _binding : FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_signup, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.sendEmailBtn.setOnClickListener {
            val nickname = binding.nicknameEt.text.toString()
            val email = binding.emailEt.text.toString()
            val password1 = binding.passwordEtFirst.text.toString()
            val password2 = binding.passwordEtSecond.text.toString()

            signup(email, password1, nickname)

           /* binding.passwordEtFirst.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    //작성 전
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //작성 중
                    //todo 작은 텍스트에 보이도록 변경해야함 (비밀번호를 8자 이상 입력해주세요)
                }
                override fun afterTextChanged(s: Editable?) {
                    var pwd = binding.passwordEtFirst.text.toString()
                    if(pwd.length >= 8){
                        //인증메일 버튼 활성화
                    }
                    else{
                        //인증메일 버튼 비활성화
                    }
                }
            })

            binding.passwordEtFirst.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    //작성 전
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //작성 중
                }
                override fun afterTextChanged(s: Editable?) {
                    val password1 = binding.passwordEtFirst.text.toString()
                    val password2 = binding.passwordEtSecond.text.toString()

                    if(password1 == password2){  //입력한 비밀번호 두개가 일치하면
                        signup(email, password1, nickname)
                    }else{
                        //todo 작은 텍스트에 보이도록 변경해야함
                        Toast.makeText(getActivity(),"비밀번호가 일치하지 않아요",Toast.LENGTH_SHORT).show()
                    }
                }
            })*/

        }

        return binding.root
    }

    private fun signup(email: String, password: String, nickname:String) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    Timber.i("회원 가입 완료")

                    val user = Firebase.auth.currentUser

                    val profileUpdates = userProfileChangeRequest {
                        displayName = nickname
                    }
                    //유저 닉네임 추가하기
                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Timber.i("닉네임 설정 완료")
                            }
                        }
                    //인증메일 보내기
                    FirebaseAuth.getInstance().currentUser!!.sendEmailVerification().addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Timber.i("확인메일을 보냈습니다")
                        }else{
                            Timber.i(task.exception.toString())
                        }
                    }
                }else if (task.exception?.message.isNullOrEmpty()) {
                    Timber.i(task.exception.toString())
                } else {
                    //입력한 계정 정보가 이미 Firebase DB에 있는 경우
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