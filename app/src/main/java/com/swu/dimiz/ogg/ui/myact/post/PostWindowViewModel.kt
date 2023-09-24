package com.swu.dimiz.ogg.ui.myact.post

import androidx.lifecycle.*
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDailyDatabaseDao
import com.swu.dimiz.ogg.oggdata.localdatabase.Instruction
import com.swu.dimiz.ogg.oggdata.localdatabase.InstructionDatabaseDao
import timber.log.Timber

class PostWindowViewModel(
    activityKey: Int = 0,
    activityValue: Int = 0,
    dataActivity: ActivitiesDailyDatabaseDao,
    dataInstruction: InstructionDatabaseDao
) : ViewModel() {

    val database = dataActivity
    private val subDatabase = dataInstruction

    private val activity = MediatorLiveData<ActivitiesDaily>()
    val gettInstructions: LiveData<List<Instruction>> = subDatabase.getDirections(activityKey, activityValue)
    fun getActivity() = activity

    private val _showPostButton = MutableLiveData<Boolean>()
    val showPostButton: LiveData<Boolean>
        get() = _showPostButton

    val textVisible = gettInstructions.map {
        it.isNotEmpty()
    }
    init {
        Timber.i("created")
        activity.addSource(database.getItem(activityKey), activity::setValue)
        _showPostButton.value = false
    }

    fun onPostButton() {
        _showPostButton.value = true
    }

    fun onNoPost() {
        _showPostButton.value = false
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("destroyed")
    }
}