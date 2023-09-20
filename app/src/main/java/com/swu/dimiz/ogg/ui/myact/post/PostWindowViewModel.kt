package com.swu.dimiz.ogg.ui.myact.post

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.swu.dimiz.ogg.oggdata.MyPostDatabaseDao
import com.swu.dimiz.ogg.oggdata.OggRepository
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class PostWindowViewModel(
    val repository: OggRepository) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // get() key값에 해당하는

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}