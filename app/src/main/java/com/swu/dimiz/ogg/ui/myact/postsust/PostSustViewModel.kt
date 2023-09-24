package com.swu.dimiz.ogg.ui.myact.postsust

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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