package com.swu.dimiz.ogg.ui.myact

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.contents.listset.listutils.ListData
import com.swu.dimiz.ogg.convertToDuration
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesExtra
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustainable
import com.swu.dimiz.ogg.oggdata.localdatabase.Instruction
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyExtra
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyList
import com.swu.dimiz.ogg.oggdata.remotedatabase.MySustainable
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId

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
    private val _extraForFirebase = MutableLiveData<ActivitiesExtra?>()
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
        val duration = item.limit - convertToDuration(item.postDate)

        for(i in sustDone.value!!) {
            if(i == item.sustId && duration > 0) {
                fireDelSust()
                return true
            }
        }
        return false
    }

    fun setExtraDone(item: ActivitiesExtra) : Boolean {
        val duration = item.limit - convertToDuration(item.postDate)

        for(i in extraDone.value!!) {
            if(i == item.extraId && duration > 0) {
                fireDelExtra()
                return true
            }
        }
        return false
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                             날짜 업데이트 : 파이어베이스 -> 룸
    private fun updateSustFromFirebase(item: MySustainable) = viewModelScope.launch {
        if(item.sustID != 0 && item.strDay != null) {
            repository.updateSustDate(item.sustID, item.strDay!!)
        }
    }

    private fun updateExtraFromFirebase(item: MyExtra) = viewModelScope.launch {
        if(item.extraID != 0 && item.strDay != null) {
            repository.updateExtraDate(item.extraID, item.strDay!!)
        }
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

    fun onNavigatedDaily() {
        _navigateToDaily.value = null
    }

    fun showSust(sust: ActivitiesSustainable) {
        _navigateToSust.value = sust
        _sustId.value = sust
    }

    fun onNavigatedSust() {
        _navigateToSust.value = null
    }

    fun showExtra(extra: ActivitiesExtra) {
        _navigateToExtra.value = extra
        _extraId.value = extra
    }

    fun onNavigatedExtra() {
        _navigateToExtra.value = null
    }

    fun onCompletedExtra() {
        _extraId.value = null
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

    private fun getExtra(id: Int) = viewModelScope.launch {
        _extraForFirebase.value = repository.getExtraDate(id)
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     firebase 초기화
    private val fireDB = Firebase.firestore
    private val fireUser = Firebase.auth.currentUser

    private var dayDoneSust : Int = 0
    private var sustlimit : Int = 0
    private var dayDoneExtra : Int = 0
    private var extralimit : Int = 0


    //오늘 활동 리스트 가져오기
    private var appUser = MyCondition()
    var today = 0
    @RequiresApi(Build.VERSION_CODES.O)
    fun fireGetDaily() = viewModelScope.launch {
        fireDB.collection("User").document(fireUser?.email.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    appUser = document.toObject<MyCondition>()!!
                    today = convertToDuration(appUser.startDate)
                    val currentDateTime = Instant.ofEpochMilli(appUser.startDate).atZone(ZoneId.systemDefault()).toLocalDateTime()
                    Timber.i("currentDateTime $currentDateTime")
                    Timber.i("today $today")
                } else {
                    Timber.i("No such document")
                }
            }
            .addOnFailureListener { exception ->
                Timber.i(exception)
            }

        val myDailyList = arrayListOf<ListData>()
        fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project${appUser.projectCount}")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val mylist = document.toObject<MyList>()
                    when (today) {
                        1 -> myDailyList.add(mylist.day1act)
                        2 -> myDailyList.add(mylist.day2act)
                        3 -> myDailyList.add(mylist.day3act)
                        4 -> myDailyList.add(mylist.day4act)
                        5 -> myDailyList.add(mylist.day5act)
                        6 -> myDailyList.add(mylist.day6act)
                        7 -> myDailyList.add(mylist.day7act)
                        8 -> myDailyList.add(mylist.day8act)
                        9 -> myDailyList.add(mylist.day9act)
                        10 -> myDailyList.add(mylist.day10act)
                        11 -> myDailyList.add(mylist.day11act)
                        12 -> myDailyList.add(mylist.day12act)
                        13 -> myDailyList.add(mylist.day13act)
                        14 -> myDailyList.add(mylist.day14act)
                        15 -> myDailyList.add(mylist.day15act)
                        16 -> myDailyList.add(mylist.day16act)
                        17 -> myDailyList.add(mylist.day17act)
                        18 -> myDailyList.add(mylist.day18act)
                        19 -> myDailyList.add(mylist.day19act)
                        20 -> myDailyList.add(mylist.day20act)
                        21 -> myDailyList.add(mylist.day21act)
                    }
                }
                Timber.i("파이어베이스 myDailyList: $myDailyList")

                myDailyList.forEach {
                    if(it.aId != 0) {
                        //이미 한 daily 개수 따지려면 today에서 dailyID 몇개있는지 판별
                        fireDB.collection("User").document(fireUser?.email.toString())
                            .collection("Project${appUser.projectCount}")
                            .document("Daily").collection(today.toString())
                            .whereEqualTo("dailyID", it.aId)
                            .count().get(AggregateSource.SERVER).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Count fetched successfully
                                    val snapshot = task.result
                                    Timber.i(" ${it.aId} Count: ${snapshot.count}")
                                } else {
                                    Timber.i("Count failed: ${task.exception}")
                                }
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Timber.i(exception)
            }
    }

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
                        dayDoneSust = convertToDuration(mysust.strDay!!)
                        mySustList.add(mysust.sustID)
                        updateSustFromFirebase(mysust)
                        //날짜 체크해서 지우기
                        getSust(mysust.sustID)
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
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.i("listen:error", e)
                    return@addSnapshotListener
                }
                myExtraList.clear()
                for (doc in value!!) {
                    val myextra = doc.toObject<MyExtra>()
                    dayDoneExtra = convertToDuration(myextra.strDay!!)
                    myExtraList.add(myextra.extraID)
                    updateExtraFromFirebase(myextra)
                    //날짜 체크해서 지우기
                    getExtra(myextra.extraID)
                }
                _extraDone.value = myExtraList
                Timber.i( "Extra result: $myExtraList")
            }
    }

    private fun fireDelSust(){
        if(setSustDone(_sustForFirebase.value!!)){
            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Sustainable").document(_sustForFirebase.value!!.sustId.toString())
                .delete()
                .addOnSuccessListener { Timber.i("Sust successfully deleted!") }
                .addOnFailureListener { e -> Timber.i( "Error deleting document $e") }
        }
    }

    private fun fireDelExtra(){
        if(setExtraDone(_extraForFirebase.value!!)){
            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Extra").document(_extraForFirebase.value!!.extraId.toString())
                .delete()
                .addOnSuccessListener { Timber.i("Extra successfully deleted!") }
                .addOnFailureListener { e -> Timber.i( "Error deleting document $e") }
        }
    }

   companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as OggApplication).repository
                MyActViewModel(repository = repository)
            }
        }
    }
}
