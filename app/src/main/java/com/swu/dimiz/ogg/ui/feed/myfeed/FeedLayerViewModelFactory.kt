package com.swu.dimiz.ogg.ui.feed.myfeed

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swu.dimiz.ogg.oggdata.MyPostDatabaseDao

class FeedLayerViewModelFactory(
    private val dataSoure: MyPostDatabaseDao,
    private val application: Application) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FeedLayerViewModel::class.java)) {
            return FeedLayerViewModel(dataSoure, application) as T
        }
        throw  IllegalArgumentException("Unknown ViewModel class")
    }
}