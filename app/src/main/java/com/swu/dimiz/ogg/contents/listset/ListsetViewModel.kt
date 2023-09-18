package com.swu.dimiz.ogg.contents.listset

import android.app.Application
import androidx.lifecycle.*
import com.swu.dimiz.ogg.oggdata.OggDatabase
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDailyDatabaseDao
import com.swu.dimiz.ogg.oggdata.localdatabase.ListDatabaseDao
import com.swu.dimiz.ogg.oggdata.localdatabase.ListSet
import kotlinx.coroutines.*
import timber.log.Timber

class ListsetViewModel(
    val database: OggDatabase,
    application: Application) : AndroidViewModel(application) {

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

    val getAllData: LiveData<List<ActivitiesDaily>>
    private val repository: OggRepository

    // ───────────────────────────────────────────────────────────────────────────────────
    // 활동 등록에서는
    // - 불러올 DB 없음
    // - 진행 상황 표시 필요


    init {
        val dailyDao = OggDatabase.getInstance(application)!!.dailyDatabaseDao
        repository = OggRepository(database)
        getAllData = repository.getAlldata.asLiveData()
        getFilters()
        Timber.i("created")
    }

    fun insert(daily: ActivitiesDaily) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(daily)
        }
    }


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