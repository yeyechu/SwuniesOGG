package com.swu.dimiz.ogg.ui.env.stamp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swu.dimiz.ogg.oggdata.OggDatabase

class StampViewModelFactory (
    private val data: OggDatabase,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(StampViewModel::class.java)) {
            return StampViewModel(data, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}