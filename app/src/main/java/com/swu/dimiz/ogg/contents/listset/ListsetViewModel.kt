package com.swu.dimiz.ogg.contents.listset

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.swu.dimiz.ogg.oggdata.localdatabase.ListDatabaseDao
import timber.log.Timber

class ListsetViewModel(
    val database: ListDatabaseDao,
    application: Application) : AndroidViewModel(application) {

    private var filter = FilterHolder()
    private val category = mutableListOf("에너지", "소비", "이동수단", "자원순환")

    private val _activityList = MutableLiveData<List<String>>()
    val activityList: LiveData<List<String>>
        get() = _activityList

    private val _navigateToSave = MutableLiveData<Boolean>()
    val navigateToSave: LiveData<Boolean>
        get() = _navigateToSave

    init {
        getFilters()
        Timber.i("created")
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
        Timber.i("destroyed")
    }
}