package com.swu.dimiz.ogg.ui.myact.post

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swu.dimiz.ogg.oggdata.MyPostDatabaseDao

class PostWindowViewModelFactory(
    private val data: MyPostDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostWindowViewModel::class.java)) {
            return PostWindowViewModel(data, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}