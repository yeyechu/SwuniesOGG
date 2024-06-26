package com.swu.dimiz.ogg.member.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.MainActivity
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.OggSnackbar
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentSigninBinding
import com.swu.dimiz.ogg.oggdata.OggDatabase
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SigninFragment: Fragment() {
    private var _binding : FragmentSigninBinding? = null
    private val binding get() = _binding!!

    init {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.Default) {
                    OggDatabase.getInstance(requireContext()).apply {
                        badgesDatabaseDao.resetBadges()
                        dailyDatabaseDao.resetDaily()
                        sustDatabaseDao.resetSustDate()
                        extraDatabaseDao.resetExtraDate()
                    }

                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_signin, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        navController.setLifecycleOwner(viewLifecycleOwner)

        //------------로그인 ------------//
        binding.signinBtn.setOnClickListener {
            val email = binding.emailEt.editText?.text.toString()
            val password = binding.passwordEt.editText?.text.toString()

            signin(email, password)
        }

        //------------회원가입 ------------//
        binding.signinToSignupBtn.setOnClickListener {
            navController.navigate(SigninFragmentDirections.actionSigninFragmentToSignupFragment())
        }
        //------------비번찾기------------//
        binding.signinToFindPasswordBtn.setOnClickListener {
            navController.navigate(SigninFragmentDirections.actionSignupFragmentToFindPasswordFragment())
        }
    }
    // ──────────────────────────────────────────────────────────────────────────────────────
    //                                       로그인 (파이어베이스)
    private fun signin(email: String, password: String) {
        val fireDB = Firebase.firestore

        OggApplication.auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //auth에 저장해둔 정보 가져오기
                val appUser = MyCondition()     //사용자 기본 정보 저장

                val fireUser = Firebase.auth.currentUser
                fireUser?.let {
                    for (profile in it.providerData) {
                        appUser.nickName = profile.displayName.toString()
                    }
                }
                // ──────────────────────────────────────────────────────────────────────────────────────
                //                   이메일 인증되어있으면 firestore & storage 저장 및 로그인
                if (FirebaseAuth.getInstance().currentUser!!.isEmailVerified) {
                    val saveUser = OggApplication.auth.currentUser?.let {
                        MyCondition(
                            email = email,
                            nickName = appUser.nickName,
                        )
                    }

                    fireDB.collection("User")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                Timber.i("사용자 이미 존재")
                            }
                            if(result.isEmpty){
                                if (saveUser != null) {
                                    fireDB.collection("User").document(email).set(saveUser)
                                    Timber.i("유저 정보 firestore 저장 완료")
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Timber.i(exception)
                        }
                    //로그인 완료 화면 이동
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)

                    SignInActivity.signInActivity!!.apply {
                        if(!isFinishing) {
                            finish()
                        }
                    }
                }
                else if (!FirebaseAuth.getInstance().currentUser!!.isEmailVerified){
                    OggSnackbar.make(requireView(), "이메일로 인증을 완료해주세요.").show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}