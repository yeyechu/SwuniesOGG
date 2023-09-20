package com.swu.dimiz.ogg.contents.listset

import android.app.Application
import androidx.lifecycle.*
import com.swu.dimiz.ogg.oggdata.OggDatabase
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import kotlinx.coroutines.*
import timber.log.Timber

class ListsetViewModel(private val repository: OggRepository) : ViewModel() {

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                         필터 적용
    private var filter = FilterHolder()
    private val category = mutableListOf("energy", "consume", "transport", "recycler")

    private val _activityFilter = MutableLiveData<List<String>>()
    val activityFilter: LiveData<List<String>>
        get() = _activityFilter

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                        클릭 핸들러

    // 완료 버튼 : suspend 구현
    // ConditionRecord에 활동시작일/활동목표 저장
    // ListSet 데이터베이스 저장
    private val _navigateToSave = MutableLiveData<Boolean>()
    val navigateToSave: LiveData<Boolean>
        get() = _navigateToSave

    // 활동 체크 버튼
    // co2 받아오기, progress바 움직이기

    // 필터 버튼
    // 필터에 쿼리 적용

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     데이터베이스 초기화
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val getAllData: LiveData<List<ActivitiesDaily>> = repository.getAlldata.asLiveData()
    // 1. ConditionRecord에서 차량정보 가져오기
    // -> init{}에서 호출 후 suspend로 구현

    val automobile = 0

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                   필요한 데이터 초기화

    private val _co2Aim = MutableLiveData<Float>()
    val co2Aim: LiveData<Float>
        get() = _co2Aim

    private val _filtered = MutableLiveData<List<ActivitiesDaily>>()
    val filtered: LiveData<List<ActivitiesDaily>>
        get() = _filtered

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                      뷰모델 초기화
    init {
        getFilters()
        Timber.i("created")
    }


    // ───────────────────────────────────────────────────────────────────────────────────



//    fun insert(daily: ActivitiesDaily) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.insert(daily)
//        }
//    }


//    fun createList() {
//        uiScope.launch {
//            val newItem = ListSet()
//            insert(newItem)
//        }
//    }

//    private suspend fun insert(day: ListSet) {
//        withContext(Dispatchers.IO) {
//            for(i in 1..21) {
//                //database.insert(day)
//            }
//        }
//    }

    fun setCo2(co2: Float) {
        _co2Aim.value = co2
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                        클릭 리스너
    fun onSaveButtonClicked() {
        _navigateToSave.value = true
    }
    fun onNavigatedToSave() {
        _navigateToSave.value = false
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                          필터
    private fun getFilters() {
        category.let {
            if( it != _activityFilter.value) {
                _activityFilter.value = it
            }
        }
    }

    fun onFilterChanged(filter: String, isChecked: Boolean) {
        if(this.filter.update(filter, isChecked)) {
            getFilters()
            }
    }

    private class FilterHolder {
        var currentValue: String? = null
            private  set

        fun update(changedFilter: String, isChecked: Boolean): Boolean {
            if(isChecked) {
                currentValue = changedFilter
                return true
            } else if (currentValue == changedFilter) {
                currentValue = null
                return true
            }
            return false
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        Timber.i("destroyed")
    }
}