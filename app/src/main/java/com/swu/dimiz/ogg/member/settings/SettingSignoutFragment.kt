package com.swu.dimiz.ogg.member.settings

import android.os.Bundle
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
import com.swu.dimiz.ogg.OggSnackbar
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.databinding.FragmentSettingSignoutBinding
import com.swu.dimiz.ogg.ui.env.EnvViewModel
import timber.log.Timber

class SettingSignoutFragment: Fragment() {

    private var _binding: FragmentSettingSignoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private val viewModel: EnvViewModel by activityViewModels()


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

                //탈퇴
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }

}