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
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustainable
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyList
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EnvViewModel : ViewModel() {

    private val disposable: Disposable? = null
    //private var currentJob: Job? = null
    // SavedStateHandle 알아보기

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                      회원 정보 저장
    private val _fakeDate = MutableLiveData<Int>()
    val fakeDate: LiveData<Int>
        get() = _fakeDate

    private val _fakeToday = MutableLiveData<Float>()
    val fakeToday: LiveData<Float>
        get() = _fakeToday

    private val _stampHolder = MutableLiveData<List<StampData>?>()
    val stampHolder: LiveData<List<StampData>?>
        get() = _stampHolder

    private var stampList = ArrayList<StampData>()

    private val _co2Holder = MutableLiveData<Float>()
    val co2Holder: LiveData<Float>
        get() = _co2Holder

    private val _leftHolder = MutableLiveData<Float>()
    val leftHolder: LiveData<Float>
        get() = _leftHolder

    private val _aimCo2 = MutableLiveData<Float>()
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
    //                                       오늘의 활동
    private val _todayList = MutableLiveData<List<ActivitiesDaily>>()
    val todayList: LiveData<List<ActivitiesDaily>>
        get() = _todayList

    private val _navigateToDaily = MutableLiveData<ActivitiesDaily?>()
    val navigateToDaily: LiveData<ActivitiesDaily?>
        get() = _navigateToDaily

    private val _dailyId = MutableLiveData<ActivitiesDaily?>()
    val dailyId: LiveData<ActivitiesDaily?>
        get() = _dailyId

    private val _navigateToCamera = MutableLiveData<Boolean>()
    val navigateToToCamera: LiveData<Boolean>
        get() = _navigateToCamera

    private val _navigateToGallery = MutableLiveData<Boolean>()
    val navigateToToGallery: LiveData<Boolean>
        get() = _navigateToGallery

    private val _navigateToChecklist = MutableLiveData<Boolean>()
    val navigateToToChecklist: LiveData<Boolean>
        get() = _navigateToChecklist

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

        _co2Holder.value = FLOAT_ZERO
        _stampHolder.value = null

        Timber.i("현재 시간: ${System.currentTimeMillis()}")
        Timber.i("─────────── 날짜 변환 확인용 로그 ───────────")
        Timber.i("10월 11일 오전 1시 19분 이후: ${convertDurationToFormatted(1696954754160)}일 경과")

        stampInitialize()
        setStampHolder(stampList)
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                   파이어베이스 함수

    fun userInit() = viewModelScope.launch {
        val appUser = MyCondition()
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

                        _userCondition.value = appUser
                        Timber.i("유저 컨디션 초기화: ${_userCondition.value}")
                    }
                } else {
                    Timber.i("사용자 기본정보 받아오기 실패")
                }
            }.addOnFailureListener { exception ->
                Timber.i(exception.toString())
            }
    }
    //──────────────────────────────────────────────────────────────────────────────────────
    //                                     룸데이터 함수

    // 여기 fakeDate 아니고 userCondition.startDate으로 갈아끼워야 함
    val layerVisible = userCondition.map {
        it.startDate == 0L //0이면 참 초기화면
        //it == INTEGER_ZERO
    }

    val date = userCondition.map {
        convertDurationToInt(it.startDate)
    }

    fun leftCo2() {
        if(_co2Holder.value!! < _aimCo2.value!!) {
           _leftHolder.value = _aimCo2.value!!.minus(_co2Holder.value!!)
        }
    }

    fun setCo2(co2: Float) {
        _aimCo2.value = co2 * CO2_WHOLE
    }

    val progressWhole = co2Holder.map {
        it.div(_aimCo2.value!!).times(100).toInt()
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

    //──────────────────────────────────────────────────────────────────────────────────────
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
            Timber.i("$stampList")
            _fakeDate.value = _fakeDate.value?.plus(1)
            resetToday()
            stampList[date] = StampData(_fakeDate.value!!, _fakeToday.value!!, 1)

            setStampHolder(stampList)
            Timber.i("$stampList")
        } else {
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
    //                                       오늘의 활동

    fun showDaily(daily: ActivitiesDaily) {
        _navigateToDaily.value = daily
        _dailyId.value = daily
    }

    fun completedDaily() {
        _navigateToDaily.value = null
    }

    fun onDailyButtonClicked(daily: ActivitiesDaily) {
        when(daily.waytoPost) {
            0 -> _navigateToCamera.value = true
            1 -> _navigateToGallery.value = true
            3 -> _navigateToChecklist.value = true
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

//    fun getDailyFromFirebase() = viewModelScope.launch {
//        val todayList = ArrayList<ListData>()
//
//        fireDB.collection("User").document(fireUser?.email.toString()).collection("Project${_userCondition.value!!.projectCount}")
//            .get()
//            .addOnSuccessListener {
//                if(it != null && it.size() != 0) {
//                    for (i in it) {
//                        val tempList = i.toObject<MyList>()
//
//                    }
//                }
//            }
//
//    }

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
    override fun onCleared() {
        if(disposable?.isDisposed == false) {
            disposable.dispose()
        }
        super.onCleared()
        Timber.i("ViewModel destroyed")
    }
}