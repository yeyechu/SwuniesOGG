package com.swu.dimiz.ogg.ui.env

import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.contents.listset.*
import com.swu.dimiz.ogg.convertDurationToFormatted
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber

data class User(
    var nickname: String = "플라스틱애호가",
    var aim: Float = AIMCO2_ONE,
    var car: Int = INTEGER_ZERO,
    var startDate:Long = 0L,
    var report: Int = INTEGER_ZERO
)
class EnvViewModel : ViewModel() {

    private val disposable: Disposable? = null
    // SavedStateHandle 알아보기

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                      회원 정보 저장
    // MyCondition LiveData 객체 필요
    private val _fakeUser = MutableLiveData<User>()
    val fakeUser: LiveData<User>
        get() = _fakeUser

    private val _fakeDate = MutableLiveData<Int>()
    val fakeDate: LiveData<Int>
        get() = _fakeDate


    private val fireDB = Firebase.firestore
    private val fireUser = Firebase.auth.currentUser

    private var appUser = MyCondition()   //사용자 기본 정보 저장   //사용자 기본 정보 저장
    fun fireInfo(){
        val docRef = fireDB.collection("User").document(fireUser?.email.toString())

        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                Timber.i( "DocumentSnapshot data: ${document.data}")
                val gotUser = document.toObject<MyCondition>()
                if (gotUser != null) {
                    appUser.nickName = gotUser.nickName
                    appUser.aim = gotUser.aim
                    appUser.car = gotUser.car
                    appUser.startDate = gotUser.startDate
                    appUser.report = gotUser.report
                }
            } else {
                Timber.i("No such document")
            }
        }
            .addOnFailureListener { exception ->
                Timber.i(exception.toString())
            }
    }
    private val _stampHolder = MutableLiveData<List<StampData>?>()
    val stampHolder: LiveData<List<StampData>?>
        get() = _stampHolder

    private val _co2Holder = MutableLiveData<Float>()
    val co2Holder: LiveData<Float>
        get() = _co2Holder

    private val _aimCo2 = MutableLiveData<Float>()

    val layerVisible = fakeDate.map {
        //it.startDate == 0L
        it == INTEGER_ZERO
    }
    val date = fakeUser.map {
        convertDurationToFormatted(it.startDate)
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                      인터랙션 감지

    private val _navigateToMyEnv = MutableLiveData<Boolean>()
    val navigateToMyEnv: LiveData<Boolean>
        get() = _navigateToMyEnv

    private val _navigateToStart = MutableLiveData<Boolean>()
    val navigateToStart: LiveData<Boolean>
        get() = _navigateToStart

    private val _expandLayout = MutableLiveData<Boolean>()
    val expandLayout: LiveData<Boolean>
        get() = _expandLayout

    init {
        Timber.i("ViewModel created")
        _fakeUser.value = User()
        _fakeDate.value = INTEGER_ZERO
        _co2Holder.value = FLOAT_ZERO
        _stampHolder.value = null
        setCo2(AIMCO2_ONE)
    }

    val leftCo2 = co2Holder.map {
        _aimCo2.value!!.times(CO2_WHOLE) - it
    }

    fun setCo2(co2: Float) {
        _aimCo2.value = co2
    }

    val progressBar = co2Holder.map {
        it.div(_co2Holder.value!!).times(100).toInt()
    }

    fun setStampHolder(item: List<StampData>) {
        _stampHolder.postValue(item)
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
        _fakeUser.value!!.startDate = System.currentTimeMillis()
        Timber.i("${_fakeUser.value}")
        Timber.i("${date.value}")
    }

    fun onNavigatedToStart() {
        _navigateToStart.value = false
    }

    fun onDateButtonClicked() {
        if (_fakeDate.value!! < DATE_WHOLE) {
            _fakeDate.value = _fakeDate.value?.plus(1)
        }
    }

    override fun onCleared() {
        if(disposable?.isDisposed == false) {
            disposable.dispose()
        }
        super.onCleared()
        Timber.i("ViewModel destroyed")
    }
}