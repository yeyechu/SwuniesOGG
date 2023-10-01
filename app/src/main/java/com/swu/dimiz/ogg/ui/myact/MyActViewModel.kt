package com.swu.dimiz.ogg.ui.myact

import android.util.Log
import androidx.lifecycle.*
import com.bumptech.glide.Glide.init
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesExtra
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustainable
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyExtra
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyList
import com.swu.dimiz.ogg.oggdata.remotedatabase.MySustainable
import timber.log.Timber

class MyActViewModel (repository: OggRepository) : ViewModel() {
    // ───────────────────────────────────────────────────────────────────────────────────
    //                                        클릭 핸들러
    private val _navigateToSelected = MutableLiveData<ActivitiesSustainable?>()
    val navigateToSelected: LiveData<ActivitiesSustainable?>
        get() = _navigateToSelected
    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     데이터베이스 초기화
//    private var viewModelJob = Job()
//    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val getAllData: LiveData<List<ActivitiesDaily>> = repository.getAlldata.asLiveData()
    val getSustData: LiveData<List<ActivitiesSustainable>> = repository.getAllSusts.asLiveData()
    val getExtraData: LiveData<List<ActivitiesExtra>> = repository.getAllextras.asLiveData()

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     firebase 초기화
    val db = Firebase.firestore
    val user = Firebase.auth.currentUser

    //todo 오늘 며칠째인지 받아오기
    private var today = 3
    private var projectCount:Int = 0    //몇회차 프로젝트

    //사용자 기본 정보
    fun fireInfo(){

        val docRef = db.collection("User").document(user?.email.toString())
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                Timber.i("DocumentSnapshot data: ${document.data}")
                val gotUser = document.toObject<MyCondition>()
                projectCount = gotUser!!.projectCount
            } else {
                Timber.i("No such document")
            }
        }.addOnFailureListener { exception ->
            Timber.i(exception.toString())
        }
    }

    //오늘 해야하는 활동 리스트
    private var myDailyList = ArrayList<Int>()
    fun fireDaily() {
        val docRef2 =  db.collection("User").document(user?.email.toString()).
        collection("project$projectCount")  //대문자 수정
        docRef2.get()
            .addOnSuccessListener { result  ->
                for (document in result ) {
                    val mylist = document.toObject<MyList>()
                    if (mylist != null) {
                        when (today) {
                            1 -> myDailyList.add(mylist.day1act!!)
                            2 -> myDailyList.add(mylist.day2act!!)
                            3 -> myDailyList.add(mylist.day3act!!)
                            4 -> myDailyList.add(mylist.day4act!!)
                            5 -> myDailyList.add(mylist.day5act!!)
                            6 -> myDailyList.add(mylist.day6act!!)
                            7 -> myDailyList.add(mylist.day7act!!)
                            8 -> myDailyList.add(mylist.day8act!!)
                            9 -> myDailyList.add(mylist.day9act!!)
                            10 -> myDailyList.add(mylist.day10act!!)
                            11 -> myDailyList.add(mylist.day11act!!)
                            12 -> myDailyList.add(mylist.day12act!!)
                            13 -> myDailyList.add(mylist.day13act!!)
                            14 -> myDailyList.add(mylist.day14act!!)
                            15 -> myDailyList.add(mylist.day15act!!)
                            16 -> myDailyList.add(mylist.day16act!!)
                            17 -> myDailyList.add(mylist.day17act!!)
                            18 -> myDailyList.add(mylist.day18act!!)
                            19 -> myDailyList.add(mylist.day19act!!)
                            20 -> myDailyList.add(mylist.day20act!!)
                            21 -> myDailyList.add(mylist.day21act!!)
                            else ->  Timber.i( "등록된 리스트가 없습니다")
                        }
                    } else Timber.i( "등록된 리스트가 없습니다")
                    Timber.i( "Daily result: $myDailyList")
                }
            }.addOnFailureListener { exception ->
                Timber.i(exception.toString())
            }
    }
    //이미 한 sust
    private var mySustList = ArrayList<Int>()
    fun fireSust(){
        //지속 가능한 활동 받아오기
        db.collection("User").document(user?.email.toString()).collection("Sustainable")
            .get()
            .addOnSuccessListener { result  ->
                for (document in result ) {
                    val mysust = document.toObject<MySustainable>()
                    if (mysust != null) {
                        mySustList.add(mysust.sustID!!)
                    }
                    Timber.i( "Sust result: $mySustList")
                }
            }.addOnFailureListener { exception ->
                Timber.i(exception.toString())
            }
    }

    //이미 한 extra
    private var myExtraList = ArrayList<Int>()
    fun fireExtra(){
        //지속 가능한 활동 받아오기
        db.collection("User").document(user?.email.toString()).collection("Extra")
            .get()
            .addOnSuccessListener { result  ->
                for (document in result ) {
                    val myextra = document.toObject<MyExtra>()
                    if (myextra != null) {
                        myExtraList.add(myextra.extraID!!)
                    }
                    Timber.i( "Extra result: $myExtraList")
                }
            }.addOnFailureListener { exception ->
                Timber.i(exception.toString())
            }
    }







    init {
        Timber.i("created")
    }

    fun showPopup(sust: ActivitiesSustainable) {
        _navigateToSelected.value = sust
    }

    fun completedPopup() {
        _navigateToSelected.value = null
    }

    override fun onCleared() {
        super.onCleared()
        //viewModelJob.cancel()
        Timber.i("destroyed")
    }
}

class MyActViewModelFactory (
    private val repository: OggRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MyActViewModel::class.java)) {
            return MyActViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}