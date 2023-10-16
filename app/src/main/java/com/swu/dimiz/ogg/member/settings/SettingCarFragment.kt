package com.swu.dimiz.ogg.member.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.textfield.TextInputEditText
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.listutils.setBackground
import com.swu.dimiz.ogg.databinding.FragmentSettingCarBinding
import timber.log.Timber

class SettingCarFragment : Fragment() {
    private val viewModel: SettingViewModel by viewModels()

    private var _binding: FragmentSettingCarBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private val forCarUser: View by lazy { binding.root.findViewById(R.id.for_car_user) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_setting_car, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()


        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationIcon(R.drawable.common_button_arrow_left_svg)

        binding.viewModel = viewModel

        val carNumberInput = binding.textInputEditText3
        val carSettingCompleteBtn = binding.carSettingCompleteBtn
        val carflow2 = binding.flow2
        val slash = binding.flowSlash

        //완료 버튼 비활성화
        carNumberInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.carNumberInputText.value = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        viewModel.carNumberInputText.observe(viewLifecycleOwner) { text ->
            val isEnabled = text.isNotBlank()
            carSettingCompleteBtn.isEnabled = isEnabled
        }

        // 버튼 클릭하면 변하는 flow 부분
        viewModel.isYesButtonClicked.observe(viewLifecycleOwner) { isClicked ->
            carflow2.isEnabled = isClicked
            slash.isEnabled = isClicked

        }

        setupButtonClickListeners()
        setupButtonStyleObservers()


        }




    private fun setupButtonClickListeners() {

        binding.carYesBtn.setOnClickListener {
            viewModel.onYesButtonClicked()
            forCarUser.visibility = View.VISIBLE

        }

        binding.carNoBtn.setOnClickListener {
            viewModel.onNoButtonClicked()
            viewModel.setCarUserVisibility(false)
            forCarUser.visibility = View.GONE

        }

        binding.electricCar.setOnClickListener {
            viewModel.onElectriCarClicked()
        }

        binding.normalCar.setOnClickListener {
            viewModel.onNormalCarClicked()
        }


    }

    //버튼 디자인 변경
    private fun setupButtonStyleObservers() {
        viewModel.isYesButtonClicked.observe(viewLifecycleOwner) { isClicked ->
            if (isClicked) {
                setButtonStyles(
                    binding.carYesBtn,
                    binding.carNoBtn,
                    binding.flow1,
                    R.drawable.common_button_skyblue,
                    R.drawable.common_button_gray,
                    R.color.white,
                    R.color.primary_gray,
                    R.drawable.check,
                    ""

                )
            }
        }

        viewModel.isNoButtonClicked.observe(viewLifecycleOwner) { isClicked ->
            if (isClicked) {
                setButtonStyles(
                    binding.carNoBtn,
                    binding.carYesBtn,
                    binding.flow1,
                    R.drawable.common_button_skyblue,
                    R.drawable.common_button_gray,
                    R.color.white,
                    R.color.primary_gray,
                    R.drawable.common_button_skyblue,
                    "1",


                )
            }
        }

        viewModel.isElectriCarClicked.observe(viewLifecycleOwner) { isClicked ->
            if (isClicked) {
                setButtonStyles(
                    binding.electricCar,
                    binding.normalCar,
                    null,
                    R.drawable.common_button_skyblue,
                    R.drawable.common_button_gray,
                    R.color.white,
                    R.color.primary_gray,
                    null,
                    "1"

                )
            }
        }

        viewModel.isNormalCarClicked.observe(viewLifecycleOwner) { isClicked ->
            if (isClicked) {
                setButtonStyles(
                    binding.normalCar,
                    binding.electricCar,
                    null,
                    R.drawable.common_button_skyblue,
                    R.drawable.common_button_gray,
                    R.color.white,
                    R.color.primary_gray,
                    null,
                    "1"
                )
            }
        }
    }

    private fun setButtonStyles(
        selectedButton: AppCompatButton,
        deselectedButton: AppCompatButton,
        optionalButton1: AppCompatButton?,
        selectedBackground: Int,
        deselectedBackground: Int,
        selectedTextColor: Int,
        deselectedTextColor: Int,
        optionalBackground1: Int?,
        selectedText:String,
    ) {
        selectedButton.setBackgroundResource(selectedBackground)
        selectedButton.setTextColor(ContextCompat.getColor(requireContext(), selectedTextColor))
        deselectedButton.setBackgroundResource(deselectedBackground)
        deselectedButton.setTextColor(ContextCompat.getColor(requireContext(), deselectedTextColor))
        optionalButton1?.let {
            optionalButton1.setBackgroundResource(optionalBackground1 ?: selectedBackground)
            optionalButton1.setTextColor(ContextCompat.getColor(requireContext(), selectedTextColor))
            optionalButton1.setText(selectedText)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.i("onDestroyView()")
    }
}
