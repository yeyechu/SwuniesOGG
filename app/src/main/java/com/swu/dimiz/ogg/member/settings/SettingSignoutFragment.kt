package com.swu.dimiz.ogg.member.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.OggSnackbar
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentSettingSignoutBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed
import com.swu.dimiz.ogg.ui.env.EnvViewModel
import timber.log.Timber

class SettingSignoutFragment: Fragment() {

    private var _binding: FragmentSettingSignoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private val viewModel: EnvViewModel by activityViewModels()

    val fireDB = Firebase.firestore
    val user = OggApplication.auth.currentUser
    val email = OggApplication.auth.currentUser?.email.toString()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_setting_signout, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        navController.setLifecycleOwner(viewLifecycleOwner)
        binding.viewModel = viewModel


        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationIcon(R.drawable.common_button_arrow_left_svg)

        binding.presentPasswordBtn.setOnClickListener {
            val presentPwd = binding.presentPassword.editText?.text.toString()

            //계정 삭제, 기본 이메일 주소 설정, 비밀번호 변경과 같이 보안에 민감한 작업을 하려면 사용자가 최근에 로그인한 적이 있어야 함
            val credential = EmailAuthProvider
                .getCredential(email, presentPwd)

            OggApplication.auth.currentUser?.reauthenticate(credential)?.addOnCompleteListener {
                Timber.i("User re-authenticated.")
                binding.presentPassword.helperText = "현재 비밀번호가 확인되었어요"
            }
                ?.addOnFailureListener {
                    binding.presentPassword.error = "올바른 비밀 번호를 입력해주세요"
                }
        }

        binding.checkBox3.setOnClickListener {
            // 체크박스가 체크되었는지 여부 확인
            val isChecked = binding.checkBox3.isChecked
            // 버튼 활성화 여부 설정
            binding.signoutBtn.isEnabled = isChecked
        }

        binding.signoutBtn.setOnClickListener {
            if(!binding.checkBox3.isChecked){
                OggSnackbar.make(view, getText(R.string.setting_toast_memberout_check).toString()).show()

            } else {
                fireDB.collection("User").document(email)
                    .delete()
                    .addOnSuccessListener { Timber.i("DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> Timber.i(e) }

                fireDB.collection("Feed")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val feed = document.toObject<Feed>()
                            feed.id = document.id
                            fireDB.collection("Feed").document(feed.id.toString())
                                .delete()
                                .addOnSuccessListener { Timber.i("DocumentSnapshot successfully deleted!") }
                                .addOnFailureListener { e -> Timber.i(e) }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Timber.i(exception)
                    }

                //탈퇴
                user?.delete()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Timber.i("User account deleted.")
                        }
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