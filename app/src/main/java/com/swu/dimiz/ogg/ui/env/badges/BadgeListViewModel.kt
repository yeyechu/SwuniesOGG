package com.swu.dimiz.ogg.ui.env.badges

import androidx.lifecycle.*
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges
import timber.log.Timber

class BadgeListViewModel(repository: OggRepository) : ViewModel() {

    val getAllData: LiveData<List<Badges>> = repository.getAllBadges.asLiveData()

    private val _navigateToSelected = MutableLiveData<Badges?>()
    val navigateToSelected: LiveData<Badges?>
        get() = _navigateToSelected

    init {
        Timber.i("created")
    }
    fun showPopup(badge: Badges) {
        _navigateToSelected.value = badge
    }

    fun completedPopup() {
        _navigateToSelected.value = null
    }

//    fun getBadgeCount() : Int {
//        getAllData.value
//    }
}

class BadgeListViewModelFactory(
    private val repository: OggRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(BadgeListViewModel::class.java)) {
            return BadgeListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}