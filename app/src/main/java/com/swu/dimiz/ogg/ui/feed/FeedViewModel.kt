package com.swu.dimiz.ogg.ui.feed

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.contents.listset.*
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class FeedViewModel(private val repository: OggRepository) : ViewModel() {

    private var currentJob: Job? = null
    // ───────────────────────────────────────────────────────────────────────────────────
    //                                         필터 적용
    private val category = mutableListOf(TOGETHER, ENERGY, CONSUME, TRANSPORT, RECYCLE)

    // 전체 피드리스트
    private val _feedList = MutableLiveData<List<Feed>>()
    val feedList: LiveData<List<Feed>>
        get() = _feedList

    // 필터링된 피드리스트
    private val _filteredList = MutableLiveData<List<Feed>>()
    val filteredList: LiveData<List<Feed>>
        get() = _filteredList

    private val _activityFilter = MutableLiveData<List<String>>()
    val activityFilter: LiveData<List<String>>
        get() = _activityFilter

    init {
        getFilters()
        onFilterChanged(TOGETHER, true)
        Timber.i("created")
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
                //_feedList.value = repository.getFiltered(filter)
            } catch (e: IOException) {
                _feedList.value = listOf()
            }
        }
    }

    fun onFilterChanged(filter: String, isChecked: Boolean) {
        if (isChecked) {
            onFilterUpdated(filter)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("destroyed")
    }

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as OggApplication).repository
                FeedViewModel(repository = repository)
            }
        }
    }
}