package com.swu.dimiz.ogg.ui.myact.post

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDailyDatabaseDao
import com.swu.dimiz.ogg.oggdata.localdatabase.InstructionDatabaseDao

class PostWindowViewModelFactory(private val activityKey: Int = 0,
                                 private val activityValue: Int = 0,
                                 private val dataActivity: ActivitiesDailyDatabaseDao,
                                 private val dataInstruction: InstructionDatabaseDao
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostWindowViewModel::class.java)) {
            return PostWindowViewModel(activityKey, activityValue, dataActivity, dataInstruction) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}