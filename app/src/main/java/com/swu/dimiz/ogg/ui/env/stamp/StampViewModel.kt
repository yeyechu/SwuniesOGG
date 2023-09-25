package com.swu.dimiz.ogg.ui.env.stamp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swu.dimiz.ogg.oggdata.OggDatabase
import kotlinx.coroutines.*
import timber.log.Timber

class StampViewModel (val database: OggDatabase, application: Application
) : AndroidViewModel(application) {

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     데이터베이스 초기화
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // 데이터베이스에서
    // 1. 날짜/닉네임/탄소목표 : ConditionRecord
    // 2. 스탬프/전체 탄소량 : StampTable
    // 3.
    private val _date = MutableLiveData<Int>()
    val date : LiveData<Int>
        get() = _date

    private val _expandLayout = MutableLiveData<Boolean>()
    val expandLayout: LiveData<Boolean>
        get() = _expandLayout

    init {
        setDate()
        Timber.i("created")
    }

    fun onExpandButtonClicked2() {
        _expandLayout.value = _expandLayout.value != true
    }

    private fun setDate() {
        uiScope.launch {
            getDate()

            // 임시 초기화
            _date.value = 1
        }
    }

    private suspend fun getDate() {
        withContext(Dispatchers.IO) {

        }
    }

    fun onExpandButtonClicked() {
        _expandLayout.value = true
    }
    fun onFoldButtonClicked() {
        _expandLayout.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        Timber.i("destroyed")
    }
}