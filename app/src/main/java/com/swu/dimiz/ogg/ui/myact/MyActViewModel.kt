package com.swu.dimiz.ogg.ui.myact

import android.net.Uri
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.convertDurationToFormatted
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesExtra
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustainable
import com.swu.dimiz.ogg.oggdata.localdatabase.Instruction
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyExtra
import com.swu.dimiz.ogg.oggdata.remotedatabase.MySustainable
import kotlinx.coroutines.launch
import timber.log.Timber

class MyActViewModel (private val repository: OggRepository) : ViewModel() {

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       오늘의 활동
    private val _todayList = MutableLiveData<List<ActivitiesDaily>>()
    val todayList: LiveData<List<ActivitiesDaily>>
        get() = _todayList

    private val _todailyId = MutableLiveData<ActivitiesDaily?>()
    val todailyId: LiveData<ActivitiesDaily?>
        get() = _todailyId

    private val _navigateToDaily = MutableLiveData<ActivitiesDaily?>()
    val navigateToDaily: LiveData<ActivitiesDaily?>
        get() = _navigateToDaily

    private val _dailyDone = MutableLiveData<ActivitiesDaily>()
    val dailyDone: LiveData<ActivitiesDaily>
        get() = _dailyDone

    private val _navigateToGallery = MutableLiveData<Boolean>()
    val navigateToToGallery: LiveData<Boolean>
        get() = _navigateToGallery

    private val _passUri = MutableLiveData<Uri?>()
    val passUri: LiveData<Uri?>
        get() = _passUri

    private val _postEventHandler = MutableLiveData<Boolean>()
    val postEventHandler: LiveData<Boolean>
        get() = _postEventHandler

    //                                       활동 디테일
    val details = MediatorLiveData<List<Instruction>>()

    val textVisible = details.map {
        it.isNotEmpty()
    }

    //                                        지속가능
    private val _navigateToSust = MutableLiveData<ActivitiesSustainable?>()
    val navigateToSust: LiveData<ActivitiesSustainable?>
        get() = _navigateToSust

    private val _sustId = MutableLiveData<ActivitiesSustainable?>()
    val sustId: LiveData<ActivitiesSustainable?>
        get() = _sustId

    private val _sustDone = MutableLiveData<List<Int>>()
    val sustDone: LiveData<List<Int>>
        get() = _sustDone

    private val _navigateToCamera = MutableLiveData<Boolean>()
    val navigateToToCamera: LiveData<Boolean>
        get() = _navigateToCamera

    private val _navigateToChecklist = MutableLiveData<Boolean>()
    val navigateToToChecklist: LiveData<Boolean>
        get() = _navigateToChecklist

    private val _navigateToCar = MutableLiveData<Boolean>()
    val navigateToToCar: LiveData<Boolean>
        get() = _navigateToCar

    //                                          특별
    private val _navigateToExtra = MutableLiveData<ActivitiesExtra?>()
    val navigateToExtra: LiveData<ActivitiesExtra?>
        get() = _navigateToExtra

    private val _extraId = MutableLiveData<ActivitiesExtra?>()
    val extraId: LiveData<ActivitiesExtra?>
        get() = _extraId

    private val _extraDone = MutableLiveData<List<Int>>()
    val extraDone: LiveData<List<Int>>
        get() = _extraDone

    private val _navigateToLink = MutableLiveData<Boolean>()
    val navigateToToLink: LiveData<Boolean>
        get() = _navigateToLink

    //                                     for파이어베이스
    private val _sustForFirebase = MutableLiveData<ActivitiesSustainable?>()
    val sustForFirebase: LiveData<ActivitiesSustainable?>
        get() = _sustForFirebase
    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     데이터베이스 초기화
    val getSustData: LiveData<List<ActivitiesSustainable>> = repository.getAllSusts.asLiveData()
    val getExtraData: LiveData<List<ActivitiesExtra>> = repository.getAllextras.asLiveData()

    // ───────────────────────────────────────────────────────────────────────────────────
    init {
        Timber.i("created")
        _sustDone.value = listOf()
        _extraDone.value = listOf()
    }

    fun setUri(data: Uri) {
        _passUri.value = data
    }

    fun resetUri() {
        _passUri.value = null
    }

    fun setDailyDone(item: ActivitiesDaily): Boolean {
        _dailyDone.value = item
        if(item.limit == item.postCount) {
            return true
        }
        return false
    }

    fun setSustDone(item: ActivitiesSustainable) : Boolean {
        val duration = item.limit - convertDurationToFormatted(item.postDate)

        for(i in sustDone.value!!) {
            if(i == item.sustId && duration > 0) {
                return true
            }
        }
        return false
    }

    fun setExtraDone(item: ActivitiesExtra) : Boolean {
        val duration = item.limit - convertDurationToFormatted(item.postDate)

        for(i in extraDone.value!!) {
            if(i == item.extraId && duration > 0) {
                return true
            }
        }
        return false
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                      디테일 팝업

    fun showDaily(daily: ActivitiesDaily) {
        _navigateToDaily.value = daily
        _todailyId.value = daily
        details.addSource(
            repository.getInstructions(daily.dailyId, daily.instructionCount),
            details::setValue
        )
    }

    fun completedDaily() {
        _navigateToDaily.value = null
    }

    fun showSust(sust: ActivitiesSustainable) {
        _navigateToSust.value = sust
        _sustId.value = sust
    }

    fun completedSust() {
        _navigateToSust.value = null
    }

    fun showExtra(extra: ActivitiesExtra) {
        _navigateToExtra.value = extra
        _extraId.value = extra
    }

    fun completedExtra() {
        _navigateToExtra.value = null
    }

    fun onGalleryButtonClicked() {
        _navigateToGallery.value = true
    }

    fun onDailyButtonClicked(daily: ActivitiesDaily) {
        when(daily.waytoPost) {
            0 -> _navigateToCamera.value = true
            1 -> _navigateToCamera.value = true
            3 -> _navigateToChecklist.value = true
        }
    }

    fun onSustButtonClicked(sust: ActivitiesSustainable) {
        when(sust.waytoPost) {
            0 -> _navigateToCamera.value = true
            4 -> _navigateToCar.value = true
            5 -> _navigateToChecklist.value = true
        }
    }

    fun onExtraButtonClicked(extra: ActivitiesExtra) {
        when(extra.waytoPost) {
            0 -> _navigateToCamera.value = true
            2 -> _navigateToLink.value = true
        }
    }

    fun onCameraCompleted() {
        _navigateToCamera.value = false
    }

    fun onGalleryCompleted() {
        _navigateToGallery.value = false
    }

    fun onChecklistCompleted() {
        _navigateToChecklist.value = false
    }

    fun onCarCompleted() {
        _navigateToCar.value = false
    }

    fun onLinkCompleted() {
        _navigateToLink.value = false
    }

    fun noClick() {}

    fun onPostCongrats() {
        _postEventHandler.value = true
    }

    fun onPostOver() {
        _postEventHandler.value = false
    }

    override fun onCleared() {
        super.onCleared()
        //viewModelJob.cancel()
        Timber.i("destroyed")
    }

    private fun getSust(id: Int) = viewModelScope.launch {
        _sustForFirebase.value = repository.getSust(id)
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     firebase 초기화
    private val fireDB = Firebase.firestore
    private val fireUser = Firebase.auth.currentUser

    //이미 한 sust
    private var mySustList = ArrayList<Int>()
    fun fireGetSust(){
        //지속 가능한 활동 받아오기
        fireDB.collection("User").document(fireUser?.email.toString()).collection("Sustainable")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i("listen:error", e)
                    return@addSnapshotListener
                }
                mySustList.clear()
                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val mysust = dc.document.toObject<MySustainable>()
                        mySustList.add(mysust.sustID!!)
                    }
                }
                _sustDone.value = mySustList
                Timber.i( "Sust result: ${_sustDone.value}")
            }
    }

    //이미 한 extra
    private var myExtraList = ArrayList<Int>()
    fun fireGetExtra(){
        //지속 가능한 활동 받아오기
        fireDB.collection("User").document(fireUser?.email.toString()).collection("Extra")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i("listen:error", e)
                    return@addSnapshotListener
                }
                myExtraList.clear()
                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val myextra = dc.document.toObject<MyExtra>()
                        myExtraList.add(myextra.extraID!!)
                    }
                }
                _extraDone.value = myExtraList
                Timber.i( "Extra result: $myExtraList")
            }
    }

    //todo sust,extra 활동 기간 끝났는지 체크해서 서버에서 지우기

   companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as OggApplication).repository
                MyActViewModel(repository = repository)
            }
        }
    }
}
