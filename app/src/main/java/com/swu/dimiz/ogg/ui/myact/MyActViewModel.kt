package com.swu.dimiz.ogg.ui.myact

import androidx.lifecycle.*
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesExtra
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustainable
import timber.log.Timber

class MyActViewModel (repository: OggRepository) : ViewModel() {
    // ───────────────────────────────────────────────────────────────────────────────────
    //                                        클릭 핸들러
    private val _navigateToSelected = MutableLiveData<ActivitiesSustainable?>()
    val navigateToSelected: LiveData<ActivitiesSustainable?>
        get() = _navigateToSelected
    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     데이터베이스 초기화
//    private var viewModelJob = Job()
//    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val getAllData: LiveData<List<ActivitiesDaily>> = repository.getAlldata.asLiveData()
    val getSustData: LiveData<List<ActivitiesSustainable>> = repository.getAllSusts.asLiveData()
    val getExtraData: LiveData<List<ActivitiesExtra>> = repository.getAllextras.asLiveData()
    init {
        Timber.i("created")
    }

    fun showPopup(sust: ActivitiesSustainable) {
        _navigateToSelected.value = sust
    }

    fun completedPopup() {
        _navigateToSelected.value = null
    }

    override fun onCleared() {
        super.onCleared()
        //viewModelJob.cancel()
        Timber.i("destroyed")
    }
}

class MyActViewModelFactory (
    private val repository: OggRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MyActViewModel::class.java)) {
            return MyActViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}