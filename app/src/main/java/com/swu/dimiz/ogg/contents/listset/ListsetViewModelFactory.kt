package com.swu.dimiz.ogg.contents.listset

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swu.dimiz.ogg.oggdata.OggDatabase
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDailyDatabaseDao
import com.swu.dimiz.ogg.oggdata.localdatabase.ListDatabaseDao

class ListsetViewModelFactory(
    private val data: OggDatabase,
    private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ListsetViewModel::class.java)) {
            return ListsetViewModel(data, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}