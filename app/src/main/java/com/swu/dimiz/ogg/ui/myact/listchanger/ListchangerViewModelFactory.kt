package com.swu.dimiz.ogg.ui.myact.listchanger

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swu.dimiz.ogg.oggdata.localdatabase.ListDatabaseDao

class ListchangerViewModelFactory(
    private val data: ListDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ListchangerViewModel::class.java)) {
            return ListchangerViewModel(data, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}