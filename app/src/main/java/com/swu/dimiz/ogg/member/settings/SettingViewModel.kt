package com.swu.dimiz.ogg.member.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swu.dimiz.ogg.R

class SettingViewModel : ViewModel() {

    //yes 버튼 클릭하면 -> 예스는 Ogg / 노는 회색처리 / 차량번호 입력 활성화 / flow1 체크됨
    //No 버튼 클릭하면 -> ??


    // Yes 버튼 스타일
    val yesButtonStyle = MutableLiveData<Int>(R.style.StyleButtonOggWhite)

    // No 버튼 상태
    val noButtonEnabled = MutableLiveData<Boolean>(true)

    // First 버튼 배경, 텍스트
    val firstButtonBackground = MutableLiveData<Int>(R.style.StyleCircle)
    val firstButtonText = MutableLiveData<String>(1.toString())


    // Yes 버튼 클릭 시 호출되는 함수
    fun onYesButtonClicked() {
        // Yes 버튼 스타일을 변경하고 No 버튼 비활성화
        yesButtonStyle.value = R.style.StyleButtonOgg
        noButtonEnabled.value = false

        // First 버튼 배경 변경
        firstButtonBackground.value = R.style.StyleCircle_ckecked
        firstButtonText.value = ""
    }

    fun onNoButtonClicked()
    {}




}

