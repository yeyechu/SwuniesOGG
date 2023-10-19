package com.swu.dimiz.ogg.contents.listset

import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.contents.listset.listutils.*
import com.swu.dimiz.ogg.convertDurationToInt
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.Instruction
import com.swu.dimiz.ogg.oggdata.remotedatabase.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ListsetViewModel(private val repository: OggRepository) : ViewModel() {

    private var currentJob: Job? = null
    //──────────────────────────────────────────────────────────────────────────────────────
    //                                   파이어베이스 변수

    private val fireDB = Firebase.firestore
    private val fireUser = Firebase.auth.currentUser

    private var today : Int = 0
    private var appUser = MyCondition()


    val getActivities: LiveData<List<ActivitiesDaily>> = repository.getAlldata.asLiveData()
    // ───────────────────────────────────────────────────────────────────────────────────
    //                                        클릭 핸들러

    //                                       활동 목표 선택
    private val _navigateToSelection = MutableLiveData<Boolean>()
    val navigateToSelection: LiveData<Boolean>
        get() = _navigateToSelection

    private val _setListaimUI = MutableLiveData<Int>()
    val setListaimUI: LiveData<Int>
        get() = _setListaimUI

    //                                          활동 선택
    private val _navigateToSave = MutableLiveData<Boolean>()
    val navigateToSave: LiveData<Boolean>
        get() = _navigateToSave

    private val _navigateToRevise = MutableLiveData<Boolean>()
    val navigateToRevise: LiveData<Boolean>
        get() = _navigateToRevise

    private val _navigateToDetail = MutableLiveData<ActivitiesDaily?>()
    val navigateToDetail: LiveData<ActivitiesDaily?>
        get() = _navigateToDetail

    private val _dailyId = MutableLiveData<ActivitiesDaily?>()
    val dailyId: LiveData<ActivitiesDaily?>
        get() = _dailyId

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                         필터 적용
    private val category = mutableListOf(ENERGY, CONSUME, TRANSPORT, RECYCLE)

    private val _filteredList = MutableLiveData<List<ActivitiesDaily>>()
    val filteredList: LiveData<List<ActivitiesDaily>>
        get() = _filteredList

    private val _dailyList = MutableLiveData<List<Int>?>()
    val dailyList: LiveData<List<Int>?>
        get() = _dailyList

    private val _activityFilter = MutableLiveData<List<String>>()
    val activityFilter: LiveData<List<String>>
        get() = _activityFilter

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                   필요한 데이터 초기화

    //                                      활동 레벨 선택
    private val _aimCo2 = MutableLiveData<Float>()
    val aimCo2: LiveData<Float>
        get() = _aimCo2

    private val _aimTitle = MutableLiveData<String?>()
    val aimTitle: LiveData<String?>
        get() = _aimTitle

    private val _aimCotent = MutableLiveData<String?>()
    val aimCotent: LiveData<String?>
        get() = _aimCotent

    //                                        활동 선택
    private var listArray = ArrayList<ListData>()
    private var todayArray = ArrayList<ActivitiesDaily>()
    private var listArrayInt = ArrayList<Int>()

    private val _listHolder = MutableLiveData<List<ListData>?>()
    val listHolder: LiveData<List<ListData>?>
        get() = _listHolder

    private val _co2Holder = MutableLiveData<Float>()
    val co2Holder: LiveData<Float>
        get() = _co2Holder

    private val _todayHolder = MutableLiveData<List<ActivitiesDaily>?>()
    val todayHolder: LiveData<List<ActivitiesDaily>?>
        get() = _todayHolder

    private val _userCondition = MutableLiveData<MyCondition>()
    val userCondition: LiveData<MyCondition>
        get() = _userCondition

    private val _userSustainable = MutableLiveData<Boolean>()
    val userSustainable: LiveData<Boolean>
        get() = _userSustainable

    private val _userMobility = MutableLiveData<Boolean>()

    private val _toastVisibility = MutableLiveData<Boolean>()
    val toastVisibility: LiveData<Boolean>
        get() = _toastVisibility

    //                                       활동 디테일
    val details = MediatorLiveData<List<Instruction>>()

    val textVisible = details.map {
        it.isNotEmpty()
    }

    private val _dailyDone = MutableLiveData<ActivitiesDaily?>()
    val dailyDone: LiveData<ActivitiesDaily?>
        get() = _dailyDone

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                      뷰모델 초기화
    init {
        // 활동 목표 설정 페이지
        setCo2(AIMCO2_ONE)
        _aimTitle.value = ""
        _aimCotent.value = ""
        _setListaimUI.value = 1

        // 활동 선택 페이지
        initListHolder()

        getFilters()
        Timber.i("created")
    }

    fun initCondition() {
        initCo2()
        _userMobility.value = _userCondition.value!!.car == 0
        resetFrequency()

    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     활동 목표 내용 결정자

    val onClickedFaceOne = setListaimUI.map {
        it == 1
    }

    val onClickedFaceTwo = setListaimUI.map {
        it == 2
    }

    val onClickedFaceThree = setListaimUI.map {
        it == 3
    }

    fun setCo2(co2: Float) {
        _aimCo2.value = co2
    }

    private fun setUI(item: Int) {
        _setListaimUI.value = item
    }

    fun setAimCo2(button: Int) {
        setUI(button)
        when (button) {
            1 -> setCo2(AIMCO2_ONE)
            2 -> setCo2(AIMCO2_TWO)
            3 -> setCo2(AIMCO2_THREE)
        }
    }

    fun setAimTitle(co2: Float) {
        when (co2) {
            AIMCO2_ONE -> {
                _aimTitle.value = setOfAimOne.first
                _aimCotent.value = setOfAimOne.second
            }
            AIMCO2_TWO -> {
                _aimTitle.value = setOfAimTwo.first
                _aimCotent.value = setOfAimTwo.second
            }
            AIMCO2_THREE -> {
                _aimTitle.value = setOfAimThree.first
                _aimCotent.value = setOfAimThree.second
            }
            else -> _aimTitle.value = "오류쓰레기"
        }
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     활동 선택 내용 결정자
    fun copyUserCondition(item: MyCondition) {
        _userCondition.value = item
    }

    private fun initCo2() {
        if(_userCondition.value!!.aim == 0f) {
            setCo2(_aimCo2.value!!)
        } else {
            _aimCo2.value = _userCondition.value!!.aim
        }
    }

    private fun plusCo2(data: Float) {
        _co2Holder.value = _co2Holder.value?.plus(data)
    }

    private fun minusCo2(data: Float) {
        _co2Holder.value = _co2Holder.value?.minus(data)
    }

    private fun initListHolder() {

        var count = LIST_SIZE

        listArray.clear()

        while (count > 0) {
            listArray.add(ListData(0, 0))
            count--
        }
        setListHolder(listArray)
    }

    fun initTodayHolder() {
        todayArray.clear()
    }

    private fun setListHolder(data: List<ListData>) {
        _listHolder.postValue(data)
    }

    private fun setTodayHolder(data: List<ActivitiesDaily>) {
        _todayHolder.postValue(data)
    }

    fun initCo2Holder() {
        _co2Holder.value = FLOAT_ZERO
    }

    private fun addCo2HolderFromFirebase(id: Int) = viewModelScope.launch {
        val sustCo2 : Float = repository.sustCo2(id)

        _co2Holder.value = _co2Holder.value!!.plus(sustCo2)
        _userSustainable.value = true

        Timber.i("${_co2Holder.value}")
    }

    private fun addCo2HolderFromListHolder(id: Int) = viewModelScope.launch {
        Timber.i("addCo2HolderFromListHolder $id")
        if(id != 0){
            val dailyCo2: Float = repository.dailyCo2(id)
            _co2Holder.value = _co2Holder.value!!.plus(dailyCo2)

            Timber.i("${_co2Holder.value}")
        }
    }

    fun addListHolder(act: ActivitiesDaily, isChecked: Boolean) {

        val amount = act.co2 * act.limit

        if (isChecked) {
            addItem(act)

            if(temp < LIST_SIZE) {
                plusCo2(amount)
                setListHolder(listArray)
                act.freq++
                update(act)
            } else {
                toastVisible()
                act.freq = 0
            }

            Timber.i("${_co2Holder.value}")
            Timber.i("$act")
        } else {
            deleteItem(act)

            if(act.freq > 0) {
                minusCo2(amount)
                setListHolder(listArray)
                act.freq--
                update(act)
                temp = 0
            }
            Timber.i("${_co2Holder.value}")
            Timber.i("$act")
        }
    }

    private var temp = 0
    private fun addItem(item: ActivitiesDaily) = viewModelScope.launch {
        var count = 0
        for (i in listArray) {
            if (i.aId == 0) {
                i.aId = item.dailyId
                i.aNumber += item.limit
                break
            } else {
                count++
            }
        }
        if (count == LIST_SIZE) {
            toastVisible()
            temp = LIST_SIZE
        }
    }

    private fun deleteItem(item: ActivitiesDaily) = viewModelScope.launch {
        for (i in listArray) {
            if (i.aId == item.dailyId) {
                i.aNumber -= item.limit
                i.aId = 0
            }
        }
    }

    val saveButtonEnabled = co2Holder.map {
        it >= _aimCo2.value!!
    }

    val progressBar = co2Holder.map {
        it.div(_aimCo2.value!!).times(100).toInt()
    }

    private fun toastVisible() {
        _toastVisibility.value = true
    }

    fun toastInvisible() {
        _toastVisibility.value = false
    }

    fun update(act: ActivitiesDaily) = viewModelScope.launch {
        repository.updateFreq(act)
    }

    fun resetFrequency() = viewModelScope.launch {
        repository.resetFreq()
    }

    fun getTodayList() = viewModelScope.launch {
        var item : ActivitiesDaily
        initListHolder()
        initTodayHolder()

        _dailyList.value = repository.getTodayListInteger()
        Timber.i("데일리 리스트 : ${dailyList.value}")

        dailyList.value?.forEach {
            item = repository.getActivityById(it)
            addItem(item)
            todayArray.add(item)
            plusCo2(item.co2 * item.limit)
        }
        setListHolder(listArray)
        setTodayHolder(todayArray)
        Timber.i("투데이 리스트 : $todayArray")
    }

    fun setDailyDone(item: ActivitiesDaily): Boolean {
        if(item.limit == item.postCount) {
            dailyDoneSet(item)
            return true
        }
        dailyDoneCompleted()
        return false
    }

    private fun dailyDoneSet(item: ActivitiesDaily) {
        _dailyDone.value = item
    }

    fun dailyDoneCompleted() {
        _dailyDone.value = null
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                        클릭 리스너

    //                              활동 목표 : 다음으로> 버튼 클릭
    fun onSelectionButtonClicked() {
        _navigateToSelection.value = true
    }

    fun onNavigatedToSelection() {
        _navigateToSelection.value = false
    }

    //                                        활동 리스트

    fun onSaveButtonClicked() {
        _navigateToSave.value = true
    }

    fun onNavigatedToSave() {
        _navigateToSave.value = false
    }

    fun onReviseButtonClicked() {
        _navigateToRevise.value = true
    }

    fun onNavigatedToRevise() {
        _navigateToRevise.value = false
    }

    fun showPopup(act: ActivitiesDaily) {
        _navigateToDetail.value = act
        _dailyId.value = act
        details.addSource(
            repository.getInstructions(act.dailyId, act.instructionCount),
            details::setValue
        )
    }

    fun completedPopup() {
        _navigateToDetail.value = null
    }

    fun noClick() {}

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                          필터
    private fun getFilters() {
        category.let {
            if (it != _activityFilter.value) {
                _activityFilter.value = it
            }
        }
    }

    private fun onFilterUpdated(filter: String) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {

            try {
                _filteredList.value = repository.getFiltered(filter)
                //_filteredList.value = getActivities.value!!.filter { it.filter == filter }
            } catch (e: IOException) {
                _filteredList.value = listOf()
            }
        }
    }

    fun onFilterChanged(filter: String, isChecked: Boolean) {
        if (isChecked) {
            onFilterUpdated(filter)
        }
    }

    // 파이어베이스에서 Room 오늘 리스트 업데이트
    private fun updateListFire(list: ListData) = viewModelScope.launch {
        repository.updateFreq(list.aId)
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                             전체활동 기본정보 추가
    private fun fireAllReset()= viewModelScope.launch {
        for (i in 10001..10020) {
            var daily = MyAllAct()
            when (i) {
                in 10001..10008 -> { daily = MyAllAct(ID = i, actCode = "에너지", upCount = 0, allC02 = 0.0) }
                10009 -> { daily = MyAllAct(ID = i, actCode = "소비", upCount = 0, allC02 = 0.0) }
                in 10010..10012 -> { daily = MyAllAct(ID = i, actCode = "이동수단", upCount = 0, allC02 = 0.0) }
                in 10013..10020 -> { daily = MyAllAct(ID = i, actCode = "자원순환", upCount = 0, allC02 = 0.0) }
            }
            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("AllAct").document(i.toString())
                .set(daily)
                .addOnSuccessListener { }
                .addOnFailureListener { e -> Timber.i(e) }
        }
        for(i in 20001 .. 20008){
            var sust = MyAllAct()
            when (i) {
                in 20001..20006 -> { sust = MyAllAct(ID = i, actCode = "에너지", upCount = 0, allC02 = 0.0) }
                in 20007..20008 -> { sust = MyAllAct(ID = i, actCode = "이동수단", upCount = 0, allC02 = 0.0) }
            }
            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("AllAct").document(i.toString())
                .set(sust)
                .addOnSuccessListener { }
                .addOnFailureListener { e -> Timber.i(e) }
        }
        for(i in 30001 .. 30007){
            var extra = MyAllAct()
            when (i) {
                in 30001..30003 -> { extra = MyAllAct(ID = i, actCode = "자원순환", upCount = 0, allC02 = 0.0) }
                in 30004..30005 -> { extra = MyAllAct(ID = i, actCode = "에너지", upCount = 0, allC02 = 0.0) }
                30006 -> { extra = MyAllAct(ID = i, actCode = "자원순환", upCount = 0, allC02 = 0.0) }
                30007 -> { extra = MyAllAct(ID = i, actCode = "소비", upCount = 0, allC02 = 0.0) }
            }
            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("AllAct").document(i.toString())
                .set(extra)
                .addOnSuccessListener { }
                .addOnFailureListener { e -> Timber.i(e) }
        }
    }
    private fun fireStampReset() = viewModelScope.launch {
        for(i in 1..21){
            val stamp = MyStamp(day = i, dayCo2 = 0.0)
            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Stamp").document(i.toString())
                .set(stamp)
                .addOnSuccessListener { }
                .addOnFailureListener { e -> Timber.i(e) }
        }
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                             기본 정보 가져오기
    fun fireInfo() = viewModelScope.launch {
        //사용자 기본 정보
        fireDB.collection("User").document(fireUser?.email.toString())
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.i( e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    appUser = snapshot.toObject<MyCondition>()!!
                    _userCondition.value = appUser
                    Timber.i("유저 컨디션 초기화: ${_userCondition.value}")
                    //today 초기화
                    if (appUser.startDate == 0L && appUser.aim == 0f) {  //처음 시작한다면
                        today =  1
                        fireAllReset()
                        fireStampReset()
                    } else {
                        today = convertDurationToInt(appUser.startDate)
                    }
                } else {
                    Timber.i("Current data: null")
                }
            }
    }

    //오늘 활동 리스트 가져오기
    fun fireGetDaily() = viewModelScope.launch {
        val myDailyList = arrayListOf<ListData>()
        fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project${_userCondition.value?.projectCount}")
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
                    Timber.i("마이데일리 리스트: $myDailyList")
                }
                for (i in 0 until myDailyList.size) {
                    listArray[i] = myDailyList[i]
                    Timber.i("listArray:  $listArray")
                    addCo2HolderFromListHolder(listArray[i].aId)
                }
                setListHolder(listArray)
                //listarray를 ActivitiesDaily로 변환해서 addlistholder에 넣어야함
                //addItem쓰기
                //getitem으로 불러오면 엑티비티 데일리로 변함
                //update호출
                //쿼리 새로생긴거 사용하기
                //서버가 가져와서 룸에 다가 저장
            }
            .addOnFailureListener { exception ->
                Timber.i(exception)
            }
    }

    //이미 한 sust
    private var mySustList = ArrayList<Int>()
    fun fireGetSust() = viewModelScope.launch {
        //지속 가능한 활동 받아오기
        fireDB.collection("User").document(fireUser?.email.toString()).collection("Sustainable")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i("listen:error", e)
                    return@addSnapshotListener
                }
                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val mysust = dc.document.toObject<MySustainable>()
                        mySustList.add(mysust.sustID!!)
                        addCo2HolderFromFirebase(mysust.sustID!!)
                    }
                }
            }
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                  firebase 리스트 저장
    //프로젝트 시작하기로 들어온 경우
    fun fireSave() = viewModelScope.launch {
        //몇번째 프로젝트 초기화
        _userCondition.value?.projectCount = +1

        //전체 리스트 편집
        for (i in 0 until LIST_SIZE) {
            val actList = MyList()
            actList.setFirstList(listArray[i].aId, listArray[i].aNumber)
            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Project${_userCondition.value?.projectCount}").document(i.toString())
                .set(actList)
                .addOnCompleteListener { Timber.i("DocumentSnapshot1 successfully written!")
                }.addOnFailureListener { e -> Timber.i("Error writing document", e) }
        }
        //프로젝트 상태 변경
        if (today == 1) {
            val toStartDay = SimpleDateFormat("yyyyMMddHHmmss").format(Date())

            val washingtonRef = fireDB.collection("User").document(fireUser?.email.toString())
            washingtonRef.update("startDate", toStartDay.toLong())
                .addOnSuccessListener { Timber.i("DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Timber.i("Error updating document", e) }

            washingtonRef.update("aim", _aimCo2.value)
                .addOnSuccessListener { Timber.i("DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Timber.i("Error updating document", e) }

            washingtonRef.update("projectCount", _userCondition.value?.projectCount)
                .addOnSuccessListener { Timber.i("DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Timber.i("Error updating document", e) }
        }
    }

    //남은활동 모두 변경하기 클릭시
    fun fireReSave() = viewModelScope.launch{
        var mylist = MyList()

        for(i in 0..LIST_SIZE){
            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Project${_userCondition.value?.projectCount}").document(i.toString())
                .get()
                .addOnSuccessListener { document ->
                    Timber.i( "전체 리스트 ${document.id} => ${document.data}")
                    var list = document.toObject<MyList>()
                    if (list != null) {
                        mylist=list
                        Timber.i( "리스트 $mylist")

                        mylist.setLeftdayList(today, listArray[i].aId, listArray[i].aNumber)
                        Timber.i( "새로운 리스트 $mylist")

                        fireDB.collection("User").document(fireUser?.email.toString())
                            .collection("Project${appUser.projectCount}").document(i.toString())
                            .set(mylist)
                            .addOnCompleteListener { Timber.i("DocumentSnapshot1 successfully written!")
                            }.addOnFailureListener { e -> Timber.i("Error writing document", e) }
                    }
                }
                .addOnFailureListener { exception ->
                    Timber.i( "Error getting documents: $exception")
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("destroyed")
    }

    private val setOfAimOne = "초보" to "3위인 바다거북"
    private val setOfAimTwo = "중수" to "2위인 바다표범"
    private val setOfAimThree = "고수" to "1위인 바키타 돌고래"

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as OggApplication).repository
                ListsetViewModel(repository = repository)
            }
        }
    }
}