package com.swu.dimiz.ogg.ui.myact

import android.net.Uri
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.contents.listset.listutils.DATE_WHOLE
import com.swu.dimiz.ogg.contents.listset.listutils.INTEGER_ZERO
import com.swu.dimiz.ogg.contents.listset.listutils.ListData
import com.swu.dimiz.ogg.convertToDuration
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesExtra
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustainable
import com.swu.dimiz.ogg.oggdata.localdatabase.Instruction
import com.swu.dimiz.ogg.oggdata.remotedatabase.*
import kotlinx.coroutines.launch
import timber.log.Timber

class MyActViewModel(private val repository: OggRepository) : ViewModel() {

    private val _userCondition = MutableLiveData<MyCondition>()
    val userCondition: LiveData<MyCondition>
        get() = _userCondition

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       오늘의 활동
    private val _todailyId = MutableLiveData<ActivitiesDaily?>()
    val todailyId: LiveData<ActivitiesDaily?>
        get() = _todailyId

    private val _navigateToDaily = MutableLiveData<ActivitiesDaily?>()
    val navigateToDaily: LiveData<ActivitiesDaily?>
        get() = _navigateToDaily

    private val _dailyDone = MutableLiveData<List<ActivitiesDaily>>()

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

    //                                          특별
    private val _navigateToExtra = MutableLiveData<ActivitiesExtra?>()
    val navigateToExtra: LiveData<ActivitiesExtra?>
        get() = _navigateToExtra

    private val _extraId = MutableLiveData<ActivitiesExtra?>()
    val extraId: LiveData<ActivitiesExtra?>
        get() = _extraId

    private val _extraDone = MutableLiveData<List<Int>>()

    //                                       이벤트 핸들러
    private val _navigateToCamera = MutableLiveData<Boolean>()
    val navigateToToCamera: LiveData<Boolean>
        get() = _navigateToCamera

    private val _navigateToChecklist = MutableLiveData<Boolean>()
    val navigateToToChecklist: LiveData<Boolean>
        get() = _navigateToChecklist

    private val _postChecklistId = MutableLiveData<Int?>()
    val postChecklistId: LiveData<Int?>
        get() = _postChecklistId

    private val _postToChecklist = MutableLiveData<Boolean>()
    val postToChecklist: LiveData<Boolean>
        get() = _postToChecklist

    private val _checkCounter = MutableLiveData<Int>()
    private val checkCounter: LiveData<Int>
        get() = _checkCounter

    private val _navigateToCar = MutableLiveData<Boolean>()
    val navigateToToCar: LiveData<Boolean>
        get() = _navigateToCar

    private val _navigateToLink = MutableLiveData<Boolean>()
    val navigateToToLink: LiveData<Boolean>
        get() = _navigateToLink

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     데이터베이스 초기화
    val getSustData: LiveData<List<ActivitiesSustainable>> = repository.getAllSusts.asLiveData()
    val getExtraData: LiveData<List<ActivitiesExtra>> = repository.getAllextras.asLiveData()

    // ───────────────────────────────────────────────────────────────────────────────────
    init {
        Timber.i("created")
        _dailyDone.value = listOf()
        _sustDone.value = listOf()
        _extraDone.value = listOf()
        _checkCounter.value = INTEGER_ZERO
    }

    fun copyUserCondition(user: MyCondition) {
        _userCondition.value = user
    }

    fun setUri(data: Uri) {
        _passUri.value = data
    }

    fun resetUri() {
        _passUri.value = null
    }

    fun setDailyDone(item: ActivitiesDaily): Boolean {

        if (item.limit <= item.postCount) {
            Timber.i("setDailyDone true: $item")
            return true
        }
        Timber.i("setDailyDone false : $item")
        return false
    }

    fun setSustDone(item: ActivitiesSustainable): Boolean {
        val duration = item.limit - convertToDuration(item.postDate)

        for (i in _sustDone.value!!) {
            if (i == item.sustId && duration > 0) {
                return true
            }
        }
        fireDelSust(item.sustId)
        return false
    }

    fun setExtraDone(item: ActivitiesExtra): Boolean {
        val duration = item.limit - convertToDuration(item.postDate)

        for (i in _extraDone.value!!) {
            if (i == item.extraId && duration > 0) {
                return true
            }
        }
        fireDelExtra(item.extraId)
        return false
    }

    fun initPostCounter() {
        _checkCounter.value = INTEGER_ZERO
    }

    private fun plusCounter() {
        _checkCounter.value = _checkCounter.value!!.plus(1)
    }

    private fun minusCounter() {
        _checkCounter.value = _checkCounter.value!!.minus(1)
    }

    fun onCheckClicked(isChecked: Boolean) {
        if(isChecked) {
            plusCounter()
        } else {
            minusCounter()
        }
    }

     val postButtonEnable = checkCounter.map {
         it > 2
     }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                             날짜 업데이트 : 파이어베이스 -> 룸
    private fun updateSustFromFirebase(item: MySustainable) = viewModelScope.launch {
        if (item.sustID != 0 && item.strDay != null) {
            repository.updateSustDate(item.sustID, item.strDay!!)
        }
    }

    private fun updateExtraFromFirebase(item: MyExtra) = viewModelScope.launch {
        if (item.extraID != 0 && item.strDay != null) {
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

    fun onGalleryButtonClicked() {
        _navigateToGallery.value = true
    }

    fun onDailyButtonClicked(daily: ActivitiesDaily) {
        when (daily.waytoPost) {
            0 -> _navigateToCamera.value = true
            1 -> _navigateToCamera.value = true
            3 -> {
                _navigateToChecklist.value = true
                _postChecklistId.value = daily.dailyId
                Timber.i("체크리스트 아이디: ${daily.dailyId}")
            }
        }
    }

    fun onSustButtonClicked(sust: ActivitiesSustainable) {
        when (sust.waytoPost) {
            0 -> _navigateToCamera.value = true
            4 -> _navigateToCar.value = true
            5 -> {
                _navigateToChecklist.value = true
                _postChecklistId.value = sust.sustId
                Timber.i("체크리스트 아이디: ${sust.sustId}")
            }
        }
    }

    fun onExtraButtonClicked(extra: ActivitiesExtra) {
        when (extra.waytoPost) {
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

    fun onPostedChecklist() {
        _postToChecklist.value = true
    }

    fun onChecklistPosted() {
        _postChecklistId.value = null
        _postToChecklist.value = false
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

    fun updateDailyPostCount() = viewModelScope.launch {
        repository.updatePostCount(todailyId.value!!.dailyId, todailyId.value!!.postCount + 1)
    }

    private fun updateDailyPostCountFromFirebase(id: Int, count: Int) = viewModelScope.launch {
        repository.updatePostCount(id, count)
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     firebase 초기화
    private val fireDB = Firebase.firestore
    private val email = OggApplication.auth.currentUser!!.email.toString()

    fun fireGetDaily() = viewModelScope.launch {
        var appUser = MyCondition()
        var today = 0

        val myDailyList = arrayListOf<ListData>()
        myDailyList.clear()

        fireDB.collection("User").document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    appUser = document.toObject<MyCondition>()!!
                    today = convertToDuration(appUser.startDate)

                    Timber.i("today $today")
                } else {
                    Timber.i("No such document")
                }
            }
            .addOnFailureListener { exception ->
                Timber.i(exception)
            }

        fireDB.collection("User").document(email)
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
                    if (it.aId != 0) {
                        //이미 한 daily 개수 따지려면 today에서 dailyID 몇개있는지 판별
                        fireDB.collection("User").document(email)
                            .collection("Project${appUser.projectCount}")
                            .document("Daily").collection(today.toString())
                            .whereEqualTo("dailyID", it.aId)
                            .count().get(AggregateSource.SERVER).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Count fetched successfully
                                    val snapshot = task.result
                                    Timber.i(" ${it.aId} Count: ${snapshot.count}")
                                    updateDailyPostCountFromFirebase(it.aId, snapshot.count.toInt())
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

    fun fireGetSust() {
        val mySustList = ArrayList<Int>()
        mySustList.clear()

        fireDB.collection("User").document(email).collection("Sustainable")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i("listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val mysust = dc.document.toObject<MySustainable>()
                        updateSustFromFirebase(mysust)
                        mySustList.add(mysust.sustID)
                    }
                }
                _sustDone.value = mySustList
                Timber.i("Sust result: ${_sustDone.value}")
            }
    }

    fun fireGetExtra() {
        val myExtraList = ArrayList<Int>()
        myExtraList.clear()
        //지속 가능한 활동 받아오기
        fireDB.collection("User").document(email).collection("Extra")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.i("listen:error", e)
                    return@addSnapshotListener
                }
                for (doc in value!!) {
                    val myextra = doc.toObject<MyExtra>()
                    updateExtraFromFirebase(myextra)
                    myExtraList.add(myextra.extraID)
                }
                _extraDone.value = myExtraList
                Timber.i("Extra result: ${_extraDone.value}t")
            }
    }

    private fun fireDelSust(id: Int) = viewModelScope.launch {
        fireDB.collection("User").document(email)
            .collection("Sustainable").document(id.toString())
            .delete()
            .addOnSuccessListener { Timber.i("Sust successfully deleted!") }
            .addOnFailureListener { e -> Timber.i("Error deleting document $e") }
    }

    private fun fireDelExtra(id: Int) = viewModelScope.launch {
        fireDB.collection("User").document(email)
            .collection("Extra").document(id.toString())
            .delete()
            .addOnSuccessListener { Timber.i("Extra $id successfully deleted!") }
            .addOnFailureListener { e -> Timber.i("Error deleting document $e") }
    }

    fun fireUpdateAll(date: Long) {
        val num = _userCondition.value!!.projectCount
        val today = convertToDuration(_userCondition.value!!.startDate)

        val daily = MyDaily(
            dailyID = 10011,
            upDate = date
        )
        fireDB.collection("User").document(email)
            .collection("Project${num}").document("Daily")
            .collection(today.toString()).document(date.toString())
            .set(daily)
            .addOnSuccessListener { Timber.i("Daily firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i(e) }

        //AllAct
        val washingtonRef = fireDB.collection("User").document(email)
            .collection("Project${num}").document("Entire")
            .collection("AllAct").document("10011")
        washingtonRef
            .update("upCount", FieldValue.increment(1))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i(e) }
        washingtonRef
            .update("allCo2", FieldValue.increment(0.28))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i(e) }

        //스탬프
        fireDB.collection("User").document(email)
            .collection("Project${num}").document("Entire")
            .collection("Stamp").document(today.toString())
            .update("dayCo2", FieldValue.increment(0.28))
            .addOnSuccessListener { Timber.i("Stamp firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i(e) }

        //배지
        //이동수단
        fireDB.collection("User").document(email)
            .collection("Badge").document("40009")
            .update("count", FieldValue.increment(1))
            .addOnSuccessListener { Timber.i("40009 올리기 완료") }
            .addOnFailureListener { e -> Timber.i(e) }

        //Co2
        fireDB.collection("User").document(email)
            .collection("Badge").document("40022")
            .update("count", FieldValue.increment(0.28))
            .addOnSuccessListener { Timber.i("40022 올리기 완료") }
            .addOnFailureListener { e -> Timber.i(e) }
        fireDB.collection("User").document(email)
            .collection("Badge").document("40023")
            .update("count", FieldValue.increment(0.28))
            .addOnSuccessListener { Timber.i("40023 올리기 완료") }
            .addOnFailureListener { e -> Timber.i(e) }
        fireDB.collection("User").document(email)
            .collection("Badge").document("40024")
            .update("count", FieldValue.increment(0.28))
            .addOnSuccessListener { Timber.i("40024 올리기 완료") }
            .addOnFailureListener { e -> Timber.i(e) }
    }

    fun fireUpdateAllSust(date: Long) {
        //배지
        //이동수단
        fireDB.collection("User").document(email)
            .collection("Badge").document("40009")
            .update("count", FieldValue.increment(1))
            .addOnSuccessListener { Timber.i("40009 올리기 완료") }
            .addOnFailureListener { e -> Timber.i(e) }

        fireDB.collection("User").document(email)
            .collection("Badge").document("40020")
            .update("count", FieldValue.increment(1))
            .addOnSuccessListener { Timber.i("40020 올리기 완료") }
            .addOnFailureListener { e -> Timber.i(e) }

        val num = _userCondition.value!!.projectCount
        val today = convertToDuration(_userCondition.value!!.startDate)

        val sust = MySustainable(
            sustID = 20007,
            strDay = date,
        )
        fireDB.collection("User").document(email)
            .collection("Sustainable").document("20007")
            .set(sust)
            .addOnSuccessListener { Timber.i("Sustainable firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i(e) }

        //AllAct
        val washingtonRef = fireDB.collection("User").document(email)
            .collection("Project${num}").document("Entire")
            .collection("AllAct").document("20007")
        washingtonRef
            .update("upCount", FieldValue.increment(1))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i(e) }
        washingtonRef
            .update("allCo2", FieldValue.increment(0.124))
            .addOnSuccessListener { Timber.i("AllAct firestore 올리기 완료") }
            .addOnFailureListener { e -> Timber.i(e) }

        //스탬프
        for (i in today..DATE_WHOLE) {
            fireDB.collection("User").document(email)
                .collection("Project${num}").document("Entire")
                .collection("Stamp").document(i.toString())
                .update("dayCo2", FieldValue.increment(0.124))
                .addOnSuccessListener { Timber.i("Stamp firestore 올리기 완료") }
                .addOnFailureListener { e -> Timber.i(e) }
        }

    }

    fun fireUpdateBadgeDate(date: Long) {
        fireDB.collection("User").document(email)
            .collection("Badge").document("40009")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val gotBadge = snapshot.toObject<MyBadge>()
                    if (gotBadge!!.count == 100 && gotBadge.getDate == null) {
                        fireDB.collection("User").document(email)
                            .collection("Badge").document("40009")
                            .update("getDate", date)
                            .addOnSuccessListener { Timber.i("40009 획득 완료") }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                } else {
                    Timber.i("Current data: null")
                }
            }
    }

    fun updateBadgeAct(date: Long) {
        Timber.i("date $date")

        fireDB.collection("User").document(email)
            .collection("Badge").document("40020")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val gotBadge = snapshot.toObject<MyBadge>()
                    if (gotBadge!!.count == 1 && gotBadge.getDate == null) {
                        fireDB.collection("User").document(email)
                            .collection("Badge").document("40020")
                            .update("getDate", date)
                            .addOnSuccessListener { Timber.i("40020 획득 완료") }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                } else {
                    Timber.i("Current data: null")
                }
            }
    }

    fun updateBadgeDateCo2(date: Long) {
        fireDB.collection("User").document(email)
            .collection("Badge").document("40022")
            .addSnapshotListener { snapshot, e ->
                if (e != null) { Timber.i(e)
                    return@addSnapshotListener }
                if (snapshot != null && snapshot.exists()) {
                    val gotBadge = snapshot.toObject<MyBadge>()
                    if (gotBadge!!.count >= 100000 && gotBadge.getDate == null) {
                        fireDB.collection("User").document(email)
                            .collection("Badge").document("40022")
                            .update("getDate", date)
                            .addOnSuccessListener { Timber.i("40022 획득 완료") }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                } else { Timber.i("Current data: null") }
            }
        fireDB.collection("User").document(email)
            .collection("Badge").document("40023")
            .addSnapshotListener { snapshot, e ->
                if (e != null) { Timber.i(e)
                    return@addSnapshotListener }
                if (snapshot != null && snapshot.exists()) {
                    val gotBadge = snapshot.toObject<MyBadge>()
                    if (gotBadge!!.count >= 500000 && gotBadge.getDate == null) {
                        fireDB.collection("User").document(email)
                            .collection("Badge").document("40023")
                            .update("getDate", date)
                            .addOnSuccessListener { Timber.i("40023 획득 완료") }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                } else { Timber.i("Current data: null") }
            }
        fireDB.collection("User").document(email)
            .collection("Badge").document("40024")
            .addSnapshotListener { snapshot, e ->
                if (e != null) { Timber.i(e)
                    return@addSnapshotListener }
                if (snapshot != null && snapshot.exists()) {
                    val gotBadge = snapshot.toObject<MyBadge>()
                    if (gotBadge!!.count >= 1000000 && gotBadge.getDate == null) {
                        fireDB.collection("User").document(email)
                            .collection("Badge").document("40024")
                            .update("getDate", date)
                            .addOnSuccessListener { Timber.i("40024 획득 완료") }
                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                    }
                } else { Timber.i("Current data: null") }
            }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as OggApplication).repository
                MyActViewModel(repository = repository)
            }
        }
    }
}
