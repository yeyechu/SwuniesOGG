package com.swu.dimiz.ogg.ui.myact.listchanger

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.swu.dimiz.ogg.oggdata.localdatabase.ListDatabaseDao
import com.swu.dimiz.ogg.oggdata.remotedatabase.ListSet
import kotlinx.coroutines.*
import timber.log.Timber

class ListchangerViewModel(
    val database: ListDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var filter = FilterHolder()
    private val category = mutableListOf("에너지", "소비", "이동수단", "자원순환")

    private val _activityList = MutableLiveData<List<String>>()
    val activityList: LiveData<List<String>>
        get() = _activityList

    private val _navigateToSave = MutableLiveData<Boolean>()
    val navigateToSave: LiveData<Boolean>
        get() = _navigateToSave

    // ───────────────────────────────────────────────────────────────────────────────────

    // 활동 수정에서는
    // 오늘 + 오늘 이후 전체를 불러올 필요가 있음
    // fun getLeftItem(num: Int) : LiveData<List<ListSet>>
    // fun getTodayItem(key: Long): ListSet
    // 목표 탄소량 불러와야함
    private val _date = MutableLiveData<Int>()
    val date: LiveData<Int>
        get() = _date

    private var today = MutableLiveData<ListSet?>()
    private val days = database.getAllItem()

    init {
        _date.value = 0
        getFilters()
        initializeToday()
        Timber.i("created")
    }

    private fun initializeToday() {
        uiScope.launch {
            today.value = getTodayFromDatabase()
        }
    }

    private suspend fun getTodayFromDatabase(): ListSet? {
//        return withContext(Dispatchers.IO) {
//            var day = database.getTodayItem(_date.value!!)
//
//            if(day?.listId != _date.value) {
//                day = null
//            }
//            day
//        }
        return null
    }

    fun createList() {
        uiScope.launch {
            val newItem = ListSet()
            insert(newItem)
            today.value = getTodayFromDatabase()
        }
    }

    private suspend fun insert(day: ListSet) {
        withContext(Dispatchers.IO) {
            for(i in 1..21) {
                database.insert(day)
            }
        }
    }

    private fun getFilters() {
        category.let {
            if( it != _activityList.value) {
                _activityList.value = it
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

    fun onSaveButtonClicked() {
        _navigateToSave.value = true
    }
    fun onNavigatedToSave() {
        _navigateToSave.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        Timber.i("destroyed")
    }
}