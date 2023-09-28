package com.swu.dimiz.ogg.ui.myact.postsust

import androidx.lifecycle.*
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustDatabaseDao
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustainable
import timber.log.Timber

class PostSustViewModel(susKey: Int = 0, data: ActivitiesSustDatabaseDao) : ViewModel() {

    val database = data
    private val sust = MediatorLiveData<ActivitiesSustainable>()
    fun getSust() = sust

    private val _showPostButton = MutableLiveData<Boolean>()
    val showPostButton: LiveData<Boolean>
        get() = _showPostButton

    init {
        Timber.i("created")
        sust.addSource(database.getItem(susKey), sust::setValue)
        _showPostButton.value = false
    }

    fun onPostButton() {
        _showPostButton.value = true
    }

    fun onNoPost() {
        _showPostButton.value = false
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("destroyed")
    }
}

class PostSustViewModelFactory(private val susKey: Int = 0,
                               private val data: ActivitiesSustDatabaseDao) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostSustViewModel::class.java)) {
            return PostSustViewModel(susKey, data) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}