package com.swu.dimiz.ogg.member.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggSnackbar
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentSettingNicknameBinding
import timber.log.Timber

class SettingNickNameFragment : Fragment() {

    private var _binding: FragmentSettingNicknameBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    private val fireUser = Firebase.auth.currentUser
    val fireDB = Firebase.firestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_setting_nickname, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        navController.setLifecycleOwner(viewLifecycleOwner)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationIcon(R.drawable.common_button_arrow_left_svg)

        binding.newNicknameEt.editText?.addTextChangedListener { text ->
            binding.changeNicknameBtn.isEnabled = text.toString().isNotBlank()
        }

        binding.changeNicknameBtn.setOnClickListener {
            val newNickname = binding.newNicknameEt.editText?.text.toString()

            //계정 닉네임 업데이트
            val profileUpdates = userProfileChangeRequest {
                    displayName = newNickname
            }
            fireUser?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        Timber.i("프로필 업데이트 완료")
                    }
                }

            //firestore 변경
            fireDB.collection("User").document(fireUser?.email.toString())
                .update("nickName", newNickname)
                .addOnSuccessListener { Timber.i("닉네임 변경 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }

            navController.navigateUp()
            OggSnackbar.make(view, getText(R.string.setting_toast_nickname_changed).toString()).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }


}