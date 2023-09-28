package com.swu.dimiz.ogg.ui.env

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

import com.swu.dimiz.ogg.convertDurationToFormatted
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber

data class User(
    var nickname: String = "플라스틱애호가",
    var aim: Float = 1.4f,
    var car: Int = 0,
    var startDate:Long = 0L,
    var report: Int = 0
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

    // MyCondition.nickname
    // MyCondition.aim
    // MyCondition.car
    // MyCondition.startDate
    val db = Firebase.firestore
    val fbuser = Firebase.auth.currentUser

    lateinit var user: User

    fun getUser(){
        val docRef = db.collection("User").document(fbuser?.email.toString())

        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                Timber.i( "DocumentSnapshot data: ${document.data}")
                val gotUser = document.toObject<MyCondition>()
                if (gotUser != null) {
                    user.nickname = gotUser.nickName
                    user.aim = gotUser.aim
                    user.car = gotUser.car
                    //user.startDate = gotUser.startDate.toString() //타입오류
                    user.report = gotUser.report
                }
            } else {
                Timber.i("No such document")
            }
        }
            .addOnFailureListener { exception ->
                Timber.i(exception.toString())
            }
    }




    val layerVisible = fakeDate.map {
        //it.startDate == 0L
        it == 0
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
        _fakeDate.value = 0
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
        if (_fakeDate.value!! < 21) {
            _fakeDate.value = _fakeDate.value?.plus(1)

            Timber.i("데이트 변경버튼 눌림 ${_fakeDate.value}")
        } else {
            Timber.i("데이트 변경 불가 ${_fakeDate.value}")
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