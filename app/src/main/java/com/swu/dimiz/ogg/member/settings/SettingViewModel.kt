package com.swu.dimiz.ogg.member.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swu.dimiz.ogg.R

class SettingViewModel : ViewModel() {

//---------------자차 유무 ------------------

    private val _isNoButtonClicked = MutableLiveData<Boolean>()
    val isNoButtonClicked: LiveData<Boolean> get() = _isNoButtonClicked

    private val _isElectriCarClicked = MutableLiveData<Boolean>()
    val isElectriCarClicked: LiveData<Boolean> get() = _isElectriCarClicked

    private val _isNormalCarClicked = MutableLiveData<Boolean>()
    val isNormalCarClicked: LiveData<Boolean> get() = _isNormalCarClicked

    val carNumberInputText = MutableLiveData<String>()


    private val _isYesButtonClicked = MutableLiveData<Boolean>()
    val isYesButtonClicked: LiveData<Boolean>
        get() = _isYesButtonClicked


    init {
        _isYesButtonClicked.value = false
        _isNoButtonClicked.value = false
        _isElectriCarClicked.value = false
        _isNormalCarClicked.value = false
    }

    fun onYesButtonClicked() {
        _isYesButtonClicked.value = true
        _isNoButtonClicked.value = false
    }

    fun onNoButtonClicked() {
        _isNoButtonClicked.value = true
        _isYesButtonClicked.value = false
    }

    fun onElectriCarClicked() {
        _isElectriCarClicked.value = true
        _isNormalCarClicked.value = false
    }

    fun onNormalCarClicked() {
        _isNormalCarClicked.value = true
        _isElectriCarClicked.value = false
    }

    fun setCarUserVisibility(isVisible: Boolean) {
        _isYesButtonClicked.value = isVisible
    }


}

