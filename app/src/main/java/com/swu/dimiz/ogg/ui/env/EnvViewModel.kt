package com.swu.dimiz.ogg.ui.env

import androidx.lifecycle.ViewModel
import timber.log.Timber

class EnvViewModel : ViewModel() {

    // 회원 이름
    // 프로젝트 진행 여부
    // SavedStateHandle 알아보기
    init {
        Timber.i("ViewModel created")
    }

    // 내가 만든 백버튼은 viewModel 소멸이 안됨
    // 확인 후
    // viewModel이 재생성되지 않게 고치기
    fun onClear() {
        onCleared()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("ViewModel destroyed")
    }
}