package com.swu.dimiz.ogg.contents.listset

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.oggdata.OggDatabase
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException

class ListsetViewModel(private val repository: OggRepository) : ViewModel() {

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                        클릭 핸들러

    //                                       활동 목표 선택

    private val _navigateToSelection = MutableLiveData<Boolean>()
    val navigateToSelection: LiveData<Boolean>
        get() = _navigateToSelection

    //                                       활동 목표 선택

    // 완료 버튼 : suspend 구현
    // ConditionRecord에 활동시작일/활동목표 저장
    // ListSet 데이터베이스 저장
    private val _navigateToSave = MutableLiveData<Boolean>()
    val navigateToSave: LiveData<Boolean>
        get() = _navigateToSave

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                         필터 적용
    private var filter = FilterHolder()
    private val category = mutableListOf("energy", "consume", "transport", "recycle")

    private val _filteredList = MutableLiveData<List<ActivitiesDaily>>()
    val filteredList: LiveData<List<ActivitiesDaily>>
        get() = _filteredList

    private val _activityFilter = MutableLiveData<List<String>>()
    val activityFilter: LiveData<List<String>>
        get() = _activityFilter

    // 활동 체크 버튼
    // co2 받아오기, progress바 움직이기

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     데이터베이스 초기화
    private var currentJob: Job? = null

    //val getAllData: LiveData<List<ActivitiesDaily>> = repository.getAlldata.asLiveData()
    // 1. ConditionRecord에서 차량정보 가져오기
    // -> init{}에서 호출 후 suspend로 구현

    val automobile = 0

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                   필요한 데이터 초기화
    private val _aimCo2 = MutableLiveData<Float>()
    val aimCo2: LiveData<Float>
        get() = _aimCo2

    private val _aimTitle = MutableLiveData<String>()
    val aimTitle: LiveData<String>
        get() = _aimTitle

    private val _aimAmount = MutableLiveData<String>()
    val aimAmount: LiveData<String>
        get() = _aimAmount

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                      뷰모델 초기화
    init {
        setCo2(AIMCO2_ONE)
        setAimTitle(AIMCO2_ONE)

        getFilters()
        onFilterChanged("energy", true)
        Timber.i("created")
    }

    // ───────────────────────────────────────────────────────────────────────────────────

    fun setCo2(co2: Float) {
        _aimCo2.value = co2
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     활동 목표 내용 결정자

    fun setAimCo2(button: Int) {
        when(button) {
            1 -> setCo2(AIMCO2_ONE)
            2 -> setCo2(AIMCO2_TWO)
            3 -> setCo2(AIMCO2_THREE)
        }
    }

    fun setAimTitle(co2: Float) {
        when(co2) {
            AIMCO2_ONE -> {
                _aimTitle.value = setOfAimOne.first!!
                _aimAmount.value = setOfAimOne.second!!
            }
            AIMCO2_TWO -> {
                _aimTitle.value = setOfAimTwo.first!!
                _aimAmount.value = setOfAimTwo.second!!
            }
            AIMCO2_THREE -> {
                _aimTitle.value = setOfAimThree.first!!
                _aimAmount.value = setOfAimThree.second!!
            }
            else -> _aimTitle.value = "오류쓰레기"
        }
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                          필터
    private fun getFilters() {
        category.let {
            if (it != _activityFilter.value) {
                _activityFilter.value = it
            }
        }
    }

    private fun onFilterUpdated(filter: String) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {

            try {
                _filteredList.value = repository.getFiltered(filter)
            } catch (e: IOException) {
                _filteredList.value = listOf()
            }
        }
    }

    fun onFilterChanged(filter: String, isChecked: Boolean) {
        if (isChecked) {
            onFilterUpdated(filter)
        }
    }

    private class FilterHolder {
        var currentValue: String? = null
            private set

        fun update(changedFilter: String, isChecked: Boolean): Boolean {
            if (isChecked) {
                currentValue = changedFilter
                return true
            } else if (currentValue == changedFilter) {
                currentValue = null
                return true
            }
            return false
        }
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                        클릭 리스너

    //                                         활동 목표
    fun onSelectionButtonClicked() {
        _navigateToSelection.value = true
    }
    fun onNavigatedToSelection() {
        _navigateToSelection.value = false
    }
    //                                        활동 리스트
    fun onSaveButtonClicked() {
        _navigateToSave.value = true
    }

    fun onNavigatedToSave() {
        _navigateToSave.value = false
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("destroyed")
    }

    private val setOfAimOne = "초보" to "3위인 바다거북"
    private val setOfAimTwo = "중수" to "2위인 바다표범"
    private val setOfAimThree = "고수" to "1위인 바키타 돌고래"

    companion object {

        const val AIMCO2_ONE = 1.4f
        const val AIMCO2_TWO = 2.78f
        const val AIMCO2_THREE = 5.22f

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as OggApplication).repository
                ListsetViewModel(repository = repository)
            }
        }
    }
}