package com.swu.dimiz.ogg.ui.env

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class EnvViewModel : ViewModel() {

    // 회원 이름
    // 프로젝트 진행 여부
    // SavedStateHandle 알아보기
    private val disposable: Disposable? = null

    private val _navigate = MutableLiveData<Boolean>()
    val navigate: LiveData<Boolean>
        get() = _navigate

    private val _navigateToStart = MutableLiveData<Boolean>()
    val navigateToStart: LiveData<Boolean>
        get() = _navigateToStart

    init {
        Timber.i("ViewModel created")
    }

    fun onFabClicked() {
        _navigate.value = true
    }

    fun onStartClicked() {
        _navigateToStart.value = true
    }

    fun onNavigated() {
        _navigate.value = false
    }

    fun onNavigatedToStart() {
        _navigateToStart.value = false
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("ViewModel destroyed")
        if(disposable?.isDisposed == false) {
            disposable?.dispose()
        }
    }
}