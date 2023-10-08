package com.swu.dimiz.ogg.contents.listset

import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.contents.listset.listutils.*
import com.swu.dimiz.ogg.convertDurationToFormatted
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.Instruction
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyExtra
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyList
import com.swu.dimiz.ogg.oggdata.remotedatabase.MySustainable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ListsetViewModel(private val repository: OggRepository) : ViewModel() {

    private var currentJob: Job? = null
    private val disposable: Disposable? = null
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
    private val todayList: LiveData<List<ActivitiesDaily>> = repository.getTodayList()

    var listArray = ArrayList<ListData>()
    private var checkArray = ArrayList<CheckStatus>()

    private val _listHolder = MutableLiveData<List<ListData>?>()
    val listHolder: LiveData<List<ListData>?>
        get() = _listHolder

    private val _checkHolder = MutableLiveData<List<CheckStatus>?>()
    val checkHolder: LiveData<List<CheckStatus>?>
        get() = _checkHolder

    private val _co2Holder = MutableLiveData<Float>()
    private val co2Holder: LiveData<Float>
        get() = _co2Holder

    private val _toastVisibility = MutableLiveData<Boolean>()
    val toastVisibility: LiveData<Boolean>
        get() = _toastVisibility

    //                                       활동 디테일
    val details = MediatorLiveData<List<Instruction>>()

    val textVisible = details.map {
        it.isNotEmpty()
    }

    private val _instructions = MutableLiveData<List<Instruction>>()
    val instructions: LiveData<List<Instruction>>
        get() = _instructions

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                      뷰모델 초기화
    init {
        // 활동 목표 설정 페이지
        setCo2(AIMCO2_ONE)
        _aimTitle.value = ""
        _aimCotent.value = ""
        _setListaimUI.value = 1

        // 활동 선택 페이지
        _co2Holder.value = FLOAT_ZERO
        _listHolder.value = null

        listInitialize()
        setListHolder(listArray)
        setNumberHolder(checkArray)

        Timber.i("오늘 리스트 : ${todayList.value}")
        getFilters()
        onFilterChanged(ENERGY, true)
        Timber.i("created")
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
    private fun initCo2Holder() {
        _co2Holder.value = FLOAT_ZERO
    }

    fun setListHolder(data: List<ListData>) {
        _listHolder.postValue(data)
    }

    private fun listInitialize() {

        var index = ID_MODIFIER
        var count = 5

        listArray.clear()
        checkArray.clear()

        if(todayList.value == null) {

            for (i in 0..4) {
                listArray.add(ListData(0, 0))
            }
        } else {
            Timber.i("기존 리스트 : $todayList")
            todayList.value!!.forEach {
                listArray.add(ListData(it.dailyId, it.freq))
                count--
            }
            while (count != 0) {
                listArray.add(ListData(0, 0))
                count--
            }
        }

        for (i in 1..DATE_WHOLE) {
            checkArray.add(CheckStatus(index++, false))
        }
    }

    fun updateItem(item: ActivitiesDaily): Boolean {
        for (i in listArray) {
            if (i.aId == item.dailyId) {
                if ((i.aNumber < item.limit)) {
                    i.aNumber += 1
                    checkArray[item.dailyId - ID_MODIFIER] = CheckStatus(item.dailyId, true)
                }
                Timber.i("업데이트 아이템 $listHolder")
                return true
            }
        }
        return false
    }

    fun addItem(item: ActivitiesDaily) {
        var count = 0
        for (i in listArray) {
            if (i.aId == 0) {
                i.aId = item.dailyId
                i.aNumber += 1
                checkArray[item.dailyId - ID_MODIFIER] = CheckStatus(item.dailyId, true)
                break
            } else {
                count++
            }
        }
        if (count == 5) {
            toastVisible()
        }
    }

    private fun setNumberHolder(data: List<CheckStatus>) {
        _checkHolder.postValue(data)
    }

    fun co2Plus(item: ActivitiesDaily) {
        if (item.limit > item.freq) {
            _co2Holder.value = _co2Holder.value?.plus(item.co2)
            item.freq++
            update(item)
        }
        Timber.i("${_co2Holder.value}")
        Timber.i("$item")
    }

    private fun toastVisible() {
        _toastVisibility.value = true
    }

    fun toastInvisible() {
        _toastVisibility.value = false
    }

//    val haveCar = automobile.map {
//        automobile != 0
//    }

    val saveButtonEnabled = co2Holder.map {
        it >= _aimCo2.value!!
    }

    val progressBar = co2Holder.map {
        it.div(_aimCo2.value!!).times(100).toInt()
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

    fun showPopup(act: ActivitiesDaily) {
        _navigateToDetail.value = act
        _dailyId.value = act
        details.addSource(repository.getInstructions(act.dailyId, act.instructionCount), details::setValue)
    }

    fun completedPopup() {
        _navigateToDetail.value = null
    }

    fun update(act: ActivitiesDaily) = viewModelScope.launch {
        repository.updateFreq(act)
    }

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

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     유저 정보 초기화

    private val fireDB = Firebase.firestore
    private val fireUser = Firebase.auth.currentUser

    private var appUser = MyCondition()   //사용자 기본 정보 저장
    private var today = 0

    fun fireInfo(){
        //사용자 기본 정보
        fireDB.collection("User").document(fireUser?.email.toString())
            .get().addOnSuccessListener { document ->
                if (document != null) {
                    val gotUser = document.toObject<MyCondition>()
                    gotUser?.let {
                        appUser.nickName = gotUser.nickName
                        appUser.aim = gotUser.aim
                        appUser.car = gotUser.car
                        appUser.startDate = gotUser.startDate
                        appUser.report = gotUser.report
                        appUser.projectCount = gotUser.projectCount
                    }
                } else {
                    Timber.i("사용자 기본정보 받아오기 실패")
                }
            }.addOnFailureListener { exception ->
                Timber.i(exception.toString())
            }

        today = if(appUser.startDate == today.toLong()){
            1
        }else{
            convertDurationToFormatted(appUser.startDate)
        }
        Timber.i( "today: $today")
    }

    //오늘 활동 리스트 가져오기
    fun fireGetDaily() {
        val myDailyList = ArrayList<ListData>()

        val docRef2 =  fireDB.collection("User").document(fireUser?.email.toString()).collection("Project${appUser.projectCount}")
        docRef2.get()
            .addOnSuccessListener { result  ->
                for (document in result ) {
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
                for(i in 0 until 5){
                    listArray[i] = myDailyList[i]
                }
                setListHolder(listArray)
            }.addOnFailureListener { exception ->
                Timber.i(exception.toString())
            }
    }
    //이미 한 sust
    private var mySustList = ArrayList<Int>()
    fun fireGetSust(){
        //지속 가능한 활동 받아오기
        fireDB.collection("User").document(fireUser?.email.toString()).collection("Sustainable")
            .get()
            .addOnSuccessListener { result  ->
                for (document in result ) {
                    val mysust = document.toObject<MySustainable>()
                    mySustList.add(mysust.sustID!!)
                    Timber.i( "Sust result: $mySustList")
                }
            }.addOnFailureListener { exception ->
                Timber.i(exception.toString())
            }
    }

    //이미 한 extra
    private var myExtraList = ArrayList<Int>()
    fun fireGetExtra(){
        //지속 가능한 활동 받아오기
        fireDB.collection("User").document(fireUser?.email.toString()).collection("Extra")
            .get()
            .addOnSuccessListener { result  ->
                for (document in result ) {
                    val myextra = document.toObject<MyExtra>()
                    myExtraList.add(myextra.extraID!!)
                    Timber.i( "Extra result: $myExtraList")
                }
            }.addOnFailureListener { exception ->
                Timber.i(exception.toString())
            }
    }


    // ───────────────────────────────────────────────────────────────────────────────────
    //                                  firebase 리스트 저장
    fun fireSave(){
        for(i in 1 until 6){
            val actList = MyList()
            actList.setFirstList(listArray[i-1].aId, listArray[i-1].aNumber)
            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Project${appUser.projectCount}").document(i.toString())
                .set(actList)
                .addOnCompleteListener {
                    Timber.i("DocumentSnapshot1 successfully written!")
                }.addOnFailureListener {  e -> Timber.i("Error writing document", e)}
        }
        if(today == 1){
            val washingtonRef = fireDB.collection("User").document(fireUser?.email.toString())

            val toStartDay = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
            washingtonRef
                .update("startDate", toStartDay.toLong())
                .addOnSuccessListener { Timber.i("DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Timber.i("Error updating document", e) }

            washingtonRef
                .update("aim", _aimCo2.value)
                .addOnSuccessListener { Timber.i("DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Timber.i("Error updating document", e) }
        }
    }

    override fun onCleared() {
        if (disposable?.isDisposed == false) {
            disposable.dispose()
        }
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