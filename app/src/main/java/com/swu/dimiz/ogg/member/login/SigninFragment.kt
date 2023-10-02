package com.swu.dimiz.ogg.member.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.swu.dimiz.ogg.MainActivity
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentSigninBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import timber.log.Timber


class SigninFragment: Fragment() {
    private var _binding : FragmentSigninBinding? = null
    private val binding get() = _binding!!

    private val fireDB = Firebase.firestore
    private val fireStorage = Firebase.storage
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_signin, container, false)

        //------------로그인 ------------//
        binding.signinBtn.setOnClickListener {
            val email = binding.emailEt.editText?.text.toString()
            val password = binding.passwordEt.editText?.text.toString()

            signin(email, password)
        }

        //------------회원가입 ------------//
        binding.signinToSignupBtn.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_signinFragment_to_signupFragment)
        }
        //------------비번찾기------------//
        binding.signinToFindPasswordBtn.setOnClickListener {
//            view?.findNavController()?.navigate(R.id.action_signinFragment_to_signupFragment)
        }
        return binding.root
    }

    // ──────────────────────────────────────────────────────────────────────────────────────
    //                                       로그인 (파이어베이스)
    private fun signin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //auth에 저장해둔 정보 가져오기
                var appUser = MyCondition()     //사용자 기본 정보 저장
                val gotuser = Firebase.auth.currentUser
                gotuser?.let {
                    for (profile in it.providerData) {
                        appUser.uid = profile.uid
                        appUser.nickName = profile.displayName.toString()
                        appUser.profileUrl = profile.photoUrl.toString()
                    }
                }
                // ──────────────────────────────────────────────────────────────────────────────────────
                //                   이메일 인증되어있으면 firestore & storage 저장 및 로그인
                if (FirebaseAuth.getInstance().currentUser!!.isEmailVerified) {
                    val saveUser = auth.currentUser?.let {
                        MyCondition(
                            uid = appUser.uid,
                            email = email,
                            password = password,
                            nickName = appUser.nickName,
                            profileUrl = appUser.profileUrl
                        )
                    }

                    //사용자 정보 저장 (firestore)
                    fireDB.collection("User")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) { //사용자 있는지 탐색
                                Timber.i("${document.id} => ${document.data}")
                            }
                            if (documents.isEmpty) {
                                // 없으면 새로 저장
                                if (saveUser != null) {
                                    fireDB.collection("User").document(email).set(saveUser)
                                    Timber.i("유저 정보 firestore 저장 완료")

                                    // ──────────────────────────────────────────────────────────────────────────────────────
                                    //                                 기본 프로필 상태 저장(storage)
                                    //프로필 변경할때 필요할 예정
    //                                        val storageRef = fireStorage.reference
    //
    //                                        var fileName = email + "/" + SimpleDateFormat("yyyyMMddHHmmss").format(Date()) // 파일명이 겹치면 안되기 떄문에 시년월일분초 지정
    //
    //                                        val riversRef = storageRef.child("profile").child(fileName)
    //                                        var  uploadTask = riversRef.putFile(profileImage)
    //
    //                                        // Register observers to listen for when the download is done or if it fails
    //                                        uploadTask.addOnFailureListener {
    //                                            // Handle unsuccessful uploads
    //                                            Timber.i("strage 실패")
    //                                        }.addOnSuccessListener { taskSnapshot ->
    //                                            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
    //                                            Timber.i("strage 성공")
    //                                        }
                                }
                            }
                        }
                        .addOnFailureListener {
                            Timber.i(task.exception.toString())
                        }
                    //로그인 완료 화면 이동
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    //todo 팝업
                    Timber.i("이메일 인증이 안되어있습니다")
                }
            }else {
                Timber.i("로그인 실패")
                Timber.i(task.exception.toString())
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}