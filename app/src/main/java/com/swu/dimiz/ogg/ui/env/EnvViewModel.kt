package com.swu.dimiz.ogg.ui.env

import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.contents.listset.listutils.*
import com.swu.dimiz.ogg.convertDurationToFormatted
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber

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

        stampInitialize()
        setStampHolder(stampList)
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                   파이어베이스 함수
    private fun userInit() {
        val docRef = fireDB.collection("User").document(fireUser?.email.toString())
        val appUser = MyCondition()
        docRef.get()
            .addOnSuccessListener {
            it?.let {
                val gotUser = it.toObject<MyCondition>()

                gotUser?.let {
                    appUser.nickName = gotUser.nickName
                    appUser.aim = gotUser.aim
                    appUser.car = gotUser.car
                    appUser.startDate = gotUser.startDate.toLong()
                    appUser.report = gotUser.report

                    _userCondition.value = appUser
                }
            }
                Timber.i("No such document")
        }
            .addOnFailureListener { exception ->
                Timber.i(exception.toString())
            }
    }
    //──────────────────────────────────────────────────────────────────────────────────────
    //                                     룸데이터 함수

    // 여기 fakeDate 아니고 userCondition.startDate으로 갈아끼워야 함
    val layerVisible = fakeDate.map {
        //it.startDate == 0L
        it == INTEGER_ZERO
    }

    // fakeUser 대신 userCondition으로 갈아끼워야 함
    val date = userCondition.map {
        convertDurationToFormatted(it.startDate)
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

    fun onDateButtonClicked() {
        val date = _fakeDate.value!!

        if (date < DATE_WHOLE) {
            if(date >= 1) {
                stampList[date - 1] = StampData(_fakeDate.value!!, _fakeToday.value!!, 2)
            }
            Timber.i("$stampList")
            _fakeDate.value = _fakeDate.value?.plus(1)
            resetToday()
            stampList[date] = StampData(_fakeDate.value!!, _fakeToday.value!!, 1)
            Timber.i("$stampList")
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

    override fun onCleared() {
        if(disposable?.isDisposed == false) {
            disposable.dispose()
        }
        super.onCleared()
        Timber.i("ViewModel destroyed")
    }
}