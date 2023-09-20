package com.swu.dimiz.ogg.ui.myact.post

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swu.dimiz.ogg.oggdata.MyPostDatabaseDao
import com.swu.dimiz.ogg.oggdata.OggRepository

class PostWindowViewModelFactory(private val repository: OggRepository) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostWindowViewModel::class.java)) {
            return PostWindowViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}