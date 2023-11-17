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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.OggSnackbar
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.convertToDuration
import com.swu.dimiz.ogg.databinding.FragmentSettingCarBinding
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyBadge
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import com.swu.dimiz.ogg.oggdata.remotedatabase.MySustainable
import timber.log.Timber
import java.util.*

class SettingCarFragment : Fragment() {
    private val viewModel: SettingViewModel by viewModels()

    private var _binding: FragmentSettingCarBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private val forCarUser: View by lazy { binding.root.findViewById(R.id.for_car_user) }
    private val carSettingComplete:  View by lazy { binding.root.findViewById(R.id.car_setting_complete_btn) }

    val fireDB = Firebase.firestore

    private var elecisChecked = false
    private var nomalisChecked = false

    private var startDate = 0L
    var today = 0
    var projectCount = 0

    private var feedDay = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_setting_car, container, false
        )
        return binding.root
    }

    private val counts = ArrayList<MyBadge>()
    private fun fireUpdateBadgeDate(userEmail: String) {
        val getDate = System.currentTimeMillis()

        fireDB.collection("User").document(userEmail)
            .collection("Badge").document("40009")
            .addSnapshotListener { snapshot, e ->
                if (e != null) { Timber.i(e)
                    return@addSnapshotListener }
                if (snapshot != null && snapshot.exists()) {
                    val gotBadge = snapshot.toObject<MyBadge>()
                    if (gotBadge!!.count == 100 && gotBadge.getDate == null) {
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40009")
                            .update("getDate", getDate)
                            .addOnSuccessListener { Timber.i("40009 획득 완료") }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                } else { Timber.i("Current data: null") }
            }
        fireDB.collection("User").document(userEmail)
            .collection("Badge").document("40021")
            .addSnapshotListener { snapshot, e ->
                if (e != null) { Timber.i(e)
                    return@addSnapshotListener }
                if (snapshot != null && snapshot.exists()) {
                    val gotBadge = snapshot.toObject<MyBadge>()
                    if (gotBadge!!.count == 1 && gotBadge.getDate == null) {
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40021")
                            .update("getDate", getDate)
                            .addOnSuccessListener { Timber.i("40021 획득 완료") }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                } else { Timber.i("Current data: null") }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = findNavController()
        navController.setLifecycleOwner(viewLifecycleOwner)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationIcon(R.drawable.common_button_arrow_left_svg)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val userEmail = OggApplication.auth.currentUser?.email.toString()

        val carNumberInput = binding.textInputEditText3
        val carSettingCompleteBtn = binding.carSettingCompleteBtn
        val carflow2 = binding.flow2
        val slash = binding.flowSlash

        binding.carSettingCompleteBtn.setOnClickListener {
            val washingtonRef = fireDB.collection("User").document(userEmail)

            if (elecisChecked) {
                val car = 1
                washingtonRef
                    .update("car", car)
                    .addOnSuccessListener { Timber.i("DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Timber.i(e) }

                val carNumber = binding.carNumberInput.editText?.text.toString()
                washingtonRef
                    .update("carNumber", carNumber)
                    .addOnSuccessListener { Timber.i("DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Timber.i(e) }

                //기본 정보
                fireDB.collection("User").document(userEmail)
                    .get().addOnSuccessListener { document ->
                        if (document != null) {
                            val gotUser = document.toObject<MyCondition>()
                            gotUser?.let {
                                startDate = gotUser.startDate
                                today = convertToDuration(startDate)
                                projectCount = gotUser.projectCount

                                //전기, 수소 자동차 구매 완료
                                feedDay = System.currentTimeMillis()

                                val sust = MySustainable(
                                    sustID = 20008,
                                    strDay = feedDay,
                                )
                                fireDB.collection("User").document(userEmail)
                                    .collection("Sustainable").document("20008")
                                    .set(sust)
                                    .addOnSuccessListener { Timber.i("Sustainable firestore 올리기 완료") }
                                    .addOnFailureListener { e -> Timber.i(e) }

                                //스탭프 업
                                for (i in today..21) {
                                    fireDB.collection("User").document(userEmail)
                                        .collection("Project${projectCount}").document("Entire")
                                        .collection("Stamp").document(i.toString())
                                        .update("dayCo2", FieldValue.increment(4.027))
                                        .addOnSuccessListener { }
                                        .addOnFailureListener { e -> Timber.i(e) }
                                }

                                //전체 활동 상태 업
                                val washingtonRef2 = fireDB.collection("User").document(userEmail)
                                    .collection("Project${projectCount}").document("Entire")
                                    .collection("AllAct").document("20008")

                                washingtonRef2
                                    .update("upCount", FieldValue.increment(1))
                                    .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
                                    .addOnFailureListener { e -> Timber.i(e) }
                                washingtonRef2
                                    .update("allCo2", FieldValue.increment(4.027))
                                    .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
                                    .addOnFailureListener { e -> Timber.i(e) }

                                // 배지 카테고리 업
                                fireDB.collection("User").document(userEmail)
                                    .collection("Badge").document("40009")
                                    .update("count", FieldValue.increment(1))
                                    .addOnSuccessListener { Timber.i("40009 올리기 완료") }
                                    .addOnFailureListener { e -> Timber.i(e) }

                                // 배지 sust 업
                                fireDB.collection("User").document(userEmail)
                                    .collection("Badge").document("40021")
                                    .update("count", FieldValue.increment(1))
                                    .addOnSuccessListener { Timber.i("40021 올리기 완료") }
                                    .addOnFailureListener { e -> Timber.i(e) }

                                fireUpdateBadgeDate(userEmail)
                            }
                        } else {
                            Timber.i("사용자 기본정보 받아오기 실패")
                        }
                    }.addOnFailureListener { exception -> Timber.i(exception.toString()) }

            } else if (nomalisChecked) {
                val car = 2
                washingtonRef
                    .update("car", car)
                    .addOnSuccessListener { Timber.i("DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Timber.i(e) }

                val carNumber = binding.carNumberInput.editText?.text.toString()
                washingtonRef
                    .update("carNumber", carNumber)
                    .addOnSuccessListener { Timber.i("DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Timber.i(e) }
            }

            findNavController().popBackStack()
            view.let { it1 -> OggSnackbar.make(it1, getText(R.string.setting_toast_car_complete).toString()).show() }


        }

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
            carSettingComplete.visibility = View.VISIBLE
            binding.backPageBtn.visibility = View.GONE

        }

        binding.carNoBtn.setOnClickListener {
            viewModel.onNoButtonClicked()
            viewModel.setCarUserVisibility(false)
            forCarUser.visibility = View.GONE
            carSettingComplete.visibility = View.GONE
            binding.backPageBtn.visibility = View.VISIBLE

        }

        binding.electricCar.setOnClickListener {
            viewModel.onElectriCarClicked()
            elecisChecked = true
            nomalisChecked = false
        }

        binding.normalCar.setOnClickListener {
            viewModel.onNormalCarClicked()
            elecisChecked = false
            nomalisChecked = true
        }

        binding.backPageBtn.setOnClickListener{
            findNavController().popBackStack()

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
        selectedText: String,
    ) {
        selectedButton.setBackgroundResource(selectedBackground)
        selectedButton.setTextColor(ContextCompat.getColor(requireContext(), selectedTextColor))
        deselectedButton.setBackgroundResource(deselectedBackground)
        deselectedButton.setTextColor(ContextCompat.getColor(requireContext(), deselectedTextColor))
        optionalButton1?.let {
            optionalButton1.setBackgroundResource(optionalBackground1 ?: selectedBackground)
            optionalButton1.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    selectedTextColor
                )
            )
            optionalButton1.text = selectedText
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        counts.clear()
        Timber.i("onDestroyView()")
    }
}
