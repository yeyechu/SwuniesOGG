package com.swu.dimiz.ogg.contents.listset

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListsetViewModel: ViewModel() {

    private var filter = FilterHolder()
    private val category = mutableListOf("에너지", "소비", "이동수단", "자원순환")

    private val _activityList = MutableLiveData<List<String>>()
    val activityList: LiveData<List<String>>
        get() = _activityList

    init {
        getFilters()
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
}