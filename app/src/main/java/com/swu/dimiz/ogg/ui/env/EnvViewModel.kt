package com.swu.dimiz.ogg.ui.env

import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.contents.listset.listutils.*
import com.swu.dimiz.ogg.convertDurationToFormatted
import com.swu.dimiz.ogg.convertDurationToInt
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyList
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyStamp
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.collections.ArrayList

class EnvViewModel : ViewModel() {

    private val _fakeDate = MutableLiveData<Int>()

    private val _fakeToday = MutableLiveData<Float>()
    val fakeToday: LiveData<Float>
        get() = _fakeToday
    //──────────────────────────────────────────────────────────────────────────────────────
    //                                      회원 정보 저장
    private val _todayCo2 = MutableLiveData<Float>()
    val todayCo2: LiveData<Float>
        get() = _todayCo2

    private var stampList = ArrayList<StampData>()

    private val _stampHolder = MutableLiveData<List<StampData>?>()
    val stampHolder: LiveData<List<StampData>?>
        get() = _stampHolder

    private val _co2Holder = MutableLiveData<Float>()
    val co2Holder: LiveData<Float>
        get() = _co2Holder

    private val _leftHolder = MutableLiveData<Float>()
    val leftHolder: LiveData<Float>
        get() = _leftHolder

    private val _aimWholeCo2 = MutableLiveData<Float>()
    //──────────────────────────────────────────────────────────────────────────────────────
    //                                      인터랙션 감지

    private val _navigateToMyEnv = MutableLiveData<Boolean>()
    val navigateToMyEnv: LiveData<Boolean>
        get() = _navigateToMyEnv

    private val _navigateToStart = MutableLiveData<Boolean>()
    val navigateToStart: LiveData<Boolean>
        get() = _navigateToStart

    private val _navigateToStartFromMyAct = MutableLiveData<Boolean>()
    val navigateToStartFromMyAct: LiveData<Boolean>
        get() = _navigateToStartFromMyAct

    private val _navigateToListset = MutableLiveData<Boolean>()
    val navigateToListset: LiveData<Boolean>
        get() = _navigateToListset

    private val _expandLayout = MutableLiveData<Boolean>()
    val expandLayout: LiveData<Boolean>
        get() = _expandLayout

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                   파이어베이스 변수

    private val fireDB = Firebase.firestore
    private val fireUser = Firebase.auth.currentUser

    private val _userCondition = MutableLiveData<MyCondition>()
    val userCondition: LiveData<MyCondition>
        get() = _userCondition

    init {
        setCo2(AIMCO2_ONE)
        userInit()
        Timber.i("ViewModel created")

        _fakeDate.value = INTEGER_ZERO
        _fakeToday.value = FLOAT_ZERO
        _todayCo2.value = FLOAT_ZERO

        _co2Holder.value = FLOAT_ZERO
        _stampHolder.value = null

        Timber.i("현재 시간: ${System.currentTimeMillis()}")
        Timber.i("─────────── 날짜 변환 확인용 로그 ───────────")
        Timber.i("10월 11일 오전 1시 19분 이후: ${convertDurationToFormatted(1696954754160)}일 경과")

        stampInitialize()
        setStampHolder(stampList)
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                     룸데이터 함수

    val layerVisible = userCondition.map {
        it.startDate == 0L
    }

    val date = userCondition.map {
        convertDurationToInt(it.startDate)
    }

    fun leftCo2() {
        if(_co2Holder.value!! < _aimWholeCo2.value!!) {
           _leftHolder.value = _aimWholeCo2.value!!.minus(_co2Holder.value!!)
        } else {
            // 목표 탄소량을 모두 채웠을 때
            // 내용 추가
        }
    }

    fun setCo2(co2: Float) {
        _aimWholeCo2.value = co2 * CO2_WHOLE
    }

    val progressWhole = co2Holder.map {
        it.div(_aimWholeCo2.value!!).times(100).toInt()
    }

    val progressDaily = fakeToday.map {
        it.div(_userCondition.value!!.aim).times(100).toInt()
    }

    private fun setStampHolder(item: List<StampData>) {
        _stampHolder.postValue(item)
    }

    private fun stampInitialize() {
        stampList.clear()

        for (i in 1..DATE_WHOLE) {
            stampList.add(StampData( i,0f, 0))
        }
    }

    private fun setStamp() {
        val tempDate = date.value!!

        if (tempDate > 0) {
            getTodayStampFromFirebase()

            if(tempDate == 1) {
                stampList[0] = StampData(tempDate, _todayCo2.value!!, 1)
            } else {
                for(i in 0..tempDate - 2) {
                    stampList[i] = StampData(today = 2)
                }
                stampList[tempDate - 1] = StampData(tempDate, _todayCo2.value!!, 1)

            }
            setStampHolder(stampList)
            Timber.i("$stampList")

        } else {
            // ───────────────────────────────────────────────────────────────────────────
            //                                   스탬프 리셋
            resetCondition()
            userInit()

            _todayCo2.value = FLOAT_ZERO
            _co2Holder.value = FLOAT_ZERO

            stampInitialize()
            setStampHolder(stampList)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────────────
    //                                       인터랙션 메서드

    fun onExpandButtonClicked() {
        _expandLayout.value = _expandLayout.value != true
    }

    fun onFabClicked() {
        _navigateToMyEnv.value = true
    }

    fun onNavigatedMyEnv() {
        _navigateToMyEnv.value = false
    }

    fun onStartClicked() {
        _navigateToStart.value = true
    }

    fun onNavigatedToStart() {
        _navigateToStart.value = false
    }

    fun onStartClickedFromMyAct() {
        _navigateToStartFromMyAct.value = true
    }

    fun onNavigatedToStartFromMyAct() {
        _navigateToStartFromMyAct.value = false
    }

    fun onListsetClicked() {
        _navigateToListset.value = true
    }

    fun onNavigatedToListset() {
        _navigateToListset.value = false
    }

    fun onDateButtonClicked() { //스탬프 넣기
        val date = _fakeDate.value!!

        if (date < DATE_WHOLE) {
            if(date >= 1) {
                stampList[date - 1] = StampData(_fakeDate.value!!, _fakeToday.value!!, 2)
                setStampHolder(stampList)
            }

            _fakeDate.value = _fakeDate.value?.plus(1)
            resetToday()
            stampList[date] = StampData(_fakeDate.value!!, _fakeToday.value!!, 1)

            setStampHolder(stampList)
            Timber.i("$stampList")
        } else {
            // ───────────────────────────────────────────────────────────────────────────
            //                                   스탬프 리셋
            resetCondition()
            userInit()
            _fakeDate.value = INTEGER_ZERO
            _fakeToday.value = FLOAT_ZERO
            _co2Holder.value = FLOAT_ZERO
            stampInitialize()
            setStampHolder(stampList)
        }
    }

    fun onCo2ButtonClicked() {
        _fakeToday.value = _fakeToday.value!!.plus(0.5f)
        _co2Holder.value = _co2Holder.value!!.plus(0.5f)
        Timber.i("${_fakeToday.value}")
    }

    private fun resetToday() {
        _fakeToday.value = FLOAT_ZERO
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                   파이어베이스 함수
    fun updateTodayStampToFirebase() = viewModelScope.launch {
        // 오늘 스탬프
        // 업데이트 할 곳
        // 오늘 co2는
        // _todayCo2.value에서 가져가면 됩니다

    }

    private fun getTodayStampFromFirebase() = viewModelScope.launch {
        // 오늘 스탬프만
        // 내려받을 곳
        // _todayCo2.value에 저장
    }

    private fun resetCondition() = viewModelScope.launch {
        val docRef = fireDB.collection("User").document(fireUser?.email.toString())

        docRef
            .update("startDate", 0L)
            .addOnSuccessListener { Timber.i("DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Timber.i("Error updating document", e) }
        docRef
            .update("aim", 0f)
            .addOnSuccessListener { Timber.i("DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Timber.i("Error updating document", e) }
    }

    private var today : Int = 0
    private var projectCount : Int = 0

    fun userInit() = viewModelScope.launch {
        var appUser: MyCondition
        //사용자 기본 정보
        fireDB.collection("User").document(fireUser?.email.toString())
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.i( e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    appUser = snapshot.toObject<MyCondition>()!!
                    today = convertDurationToInt(appUser.startDate)
                    projectCount = appUser.projectCount

                    _userCondition.value = appUser
                    setStamp()
                    Timber.i(_userCondition.toString())
                } else {
                    Timber.i("Current data: null")
                }
            }
    }

    fun fireGetStamp(){
        val stampList = arrayListOf<MyStamp>()

        fireDB.collection("User").document(fireUser?.email.toString()).collection("Stamp")
            .orderBy("day")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i("listen:error $e")
                    return@addSnapshotListener
                }
                stampList.clear()
                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val stamp = dc.document.toObject<MyStamp>()
                        stampList.add(stamp)

                        if(stamp.day == date.value) {
                            _todayCo2.value = stamp.dayCo2.toFloat()
                        }
                    }
                }
                Timber.i("스탬프 $stampList")
            }
    }

    fun fireGetDaily() = viewModelScope.launch {
        val myDailyList = ArrayList<ListData>()
        fireDB.collection("User").document(fireUser?.email.toString()).collection("Project${projectCount}")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i("listen:error", e)
                    return@addSnapshotListener
                }
                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val mylist = dc.document.toObject<MyList>()
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
                }
                /*for(i in 0 until LIST_SIZE){
                       listArray[i] = myDailyList[i]
                   }*/
                //setListHolder(listArray)
            }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("ViewModel destroyed")
    }
}