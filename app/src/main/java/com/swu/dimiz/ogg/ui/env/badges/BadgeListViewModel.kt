package com.swu.dimiz.ogg.ui.env.badges

import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class BadgeListViewModel(private val repository: OggRepository) : ViewModel() {

    private var currentJob: Job? = null

    val getAllData: LiveData<List<Badges>> = repository.getAllBadges.asLiveData()
    val inventory: LiveData<List<Badges>> = repository.getInventory()

    val inventorySize = inventory.map {
        it.size
    }

    val inventoryNull = inventorySize.map {
        it == 0
    }

    private val _navigateToSelected = MutableLiveData<Badges?>()
    val navigateToSelected: LiveData<Badges?>
        get() = _navigateToSelected

    private val _sustId = MutableLiveData<Badges?>()
    val sustId: LiveData<Badges?>
        get() = _sustId

    private val _badgeFilter = MutableLiveData<List<String>>()
    val badgeFilter: LiveData<List<String>>
        get() = _badgeFilter

    private val _badgeFilteredList = MutableLiveData<List<String>>()
    val badgeFilteredList: LiveData<List<String>>
        get() = _badgeFilteredList

    private val _badgeFilteredListTitle = MutableLiveData<List<List<String>>>()
    val badgeFilteredListTitle: LiveData<List<List<String>>>
        get() = _badgeFilteredListTitle

    init {
        Timber.i("created")
        getFilters()
    }

    fun showPopup(badge: Badges) {
        _navigateToSelected.value = badge
        _sustId.value = badge
    }

    fun completedPopup() {
        _navigateToSelected.value = null
    }

    private fun getFilters() {
        currentJob?.cancel()

        currentJob = viewModelScope.launch {
            try {
                _badgeFilter.value = repository.getFilter()
                onFilterUpdated()
            } catch (e: IOException) {
                _badgeFilter.value = listOf()
            }
        }
    }

//    private fun onFilterUpdated(filter: String) {
//        currentJob?.cancel()
//
//        currentJob = viewModelScope.launch {
//
//            try {
//                _badgeFilteredList.value = repository.getFilteredList(filter)
//            } catch (e: IOException) {
//                _badgeFilteredList.value = listOf()
//            }
//        }
//    }
private fun onFilterUpdated() {
    currentJob?.cancel()

    currentJob = viewModelScope.launch {

        try {
            _badgeFilter.value!!.forEach {
                _badgeFilteredList.value = repository.getFilteredListTitle(it)
            }
        } catch (e: IOException) {
            _badgeFilteredList.value = listOf()
        }
    }
}

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as OggApplication).repository
                BadgeListViewModel(repository = repository)
            }
        }
    }
}