package com.swu.dimiz.ogg.ui.myact.postsust

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustDatabaseDao

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
