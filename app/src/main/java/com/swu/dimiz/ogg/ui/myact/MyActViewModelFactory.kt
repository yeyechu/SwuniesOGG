package com.swu.dimiz.ogg.ui.myact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swu.dimiz.ogg.oggdata.OggRepository

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