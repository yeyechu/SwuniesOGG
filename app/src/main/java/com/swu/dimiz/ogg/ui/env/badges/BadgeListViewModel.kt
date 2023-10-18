package com.swu.dimiz.ogg.ui.env.badges

import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.Badges
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyBadge
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class BadgeListViewModel(private val repository: OggRepository) : ViewModel() {

    private var currentJob: Job? = null

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

    private val _badgeId = MutableLiveData<Badges?>()
    val badgeId: LiveData<Badges?>
        get() = _badgeId

    private val _badgeFilter = MutableLiveData<List<String>>()
    val badgeFilter: LiveData<List<String>>
        get() = _badgeFilter

    init {
        Timber.i("created")
        getFilters()
    }

    private fun showPopup(badge: Badges) {
        _navigateToSelected.value = badge
        _badgeId.value = badge
    }

    fun showBadgeDetail(badge: Int) = viewModelScope.launch {
        val badgeItem: Badges = repository.getBadge(badge)
        showPopup(badgeItem)
    }

    fun completedPopup() {
        _navigateToSelected.value = null
    }

    fun noClick() {}

    private fun getFilters() {
        currentJob?.cancel()

        currentJob = viewModelScope.launch {
            try {
                _badgeFilter.value = repository.getFilter()
            } catch (e: IOException) {
                _badgeFilter.value = listOf()
            }
        }
    }
    // 파이어베이스에서 오는 배지 -> 룸으로 업데이트
    private fun updateBadgeFire(badge: MyBadge) = viewModelScope.launch {
        repository.updateBadge(badge.badgeID!!, badge.getDate!!, badge.count)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as OggApplication).repository
                BadgeListViewModel(repository = repository)
            }
        }
    }

    //겟데이트가 있는지 카운트 업데이트
}