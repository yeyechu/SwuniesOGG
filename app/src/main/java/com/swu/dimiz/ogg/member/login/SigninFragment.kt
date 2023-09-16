package com.swu.dimiz.ogg.member.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentSigninBinding
import timber.log.Timber

class SigninFragment: Fragment() {
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore

    private lateinit var binding: FragmentSigninBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_signin, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.signInBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()

            signin(email, password)
        }

        binding.signUpBtn.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_signinFragment_to_signupFragment)
        }

        return binding.root
    }

    private fun signin(email: String, password: String) {
        auth?.signInWithEmailAndPassword(email,password)
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    if(FirebaseAuth.getInstance().currentUser!!.isEmailVerified){
                        val nickname = auth.currentUser?.displayName.toString()
                        val user = User(email, password, nickname)

                        //사용자 정보 저장
                        firestore.collection("User")
                            .whereEqualTo("email", "")
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) { //사용자 있는지 탐색
                                    Timber.i("${document.id} => ${document.data}")
                                }
                                if(documents.isEmpty){ //없으면 새로 저장
                                    firestore.collection("User").document(email)?.set(user)
                                }
                            }
                            .addOnFailureListener { exception ->
                                Timber.i(task.exception.toString())
                            }
                        //로그인 완료 화면 이동
                        view?.findNavController()?.navigate(R.id.action_fragment_login_to_navigation_main)
                    }else {
                        //todo 팝업
                        Timber.i("이메일 인증이 안되어있습니다")
                    }
                }else {
                    //로그인에 실패한 경우 Toast 메시지로 에러를 띄워준다
                    Timber.i(task.exception.toString())
                }
            }
    }
}