package com.swu.dimiz.ogg.ui.myact

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesExtra
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustainable
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyExtra
import com.swu.dimiz.ogg.oggdata.remotedatabase.MySustainable
import timber.log.Timber

class MyActViewModel (private val repository: OggRepository) : ViewModel() {
    // ───────────────────────────────────────────────────────────────────────────────────
    //                                        지속가능
    private val _navigateToSust = MutableLiveData<ActivitiesSustainable?>()
    val navigateToSust: LiveData<ActivitiesSustainable?>
        get() = _navigateToSust

    private val _sustId = MutableLiveData<ActivitiesSustainable?>()
    val sustId: LiveData<ActivitiesSustainable?>
        get() = _sustId

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
    }

    fun showSust(sust: ActivitiesSustainable) {
        _navigateToSust.value = sust
        _sustId.value = sust
    }

    fun completedSust() {
        _navigateToSust.value = null
    }

    fun showExtra(extra: ActivitiesExtra) {
        _navigateToExtra.value = extra
        _extraId.value = extra
    }

    fun completedExtra() {
        _navigateToExtra.value = null
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

    fun onChecklistCompleted() {
        _navigateToChecklist.value = false
    }

    fun onCarCompleted() {
        _navigateToCar.value = false
    }

    fun onLinkCompleted() {
        _navigateToLink.value = false
    }

    override fun onCleared() {
        super.onCleared()
        //viewModelJob.cancel()
        Timber.i("destroyed")
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     firebase 초기화
    private val fireDB = Firebase.firestore
    private val fireUser = Firebase.auth.currentUser

    //todo 오늘 며칠째인지 받아오기
    private var today = 0
    private var appUser = MyCondition()   //사용자 기본 정보 저장

    //사용자 기본 정보
//    fun fireInfo(){
//        fireDB.collection("User").document(fireUser?.email.toString())
//            .get().addOnSuccessListener { document ->
//                if (document != null) {
//                    val gotUser = document.toObject<MyCondition>()
//                    gotUser?.let {
//                        appUser.nickName = gotUser.nickName
//                        appUser.aim = gotUser.aim
//                        appUser.car = gotUser.car
//                        appUser.startDate = gotUser.startDate
//                        appUser.report = gotUser.report
//                        appUser.projectCount = gotUser.projectCount
//                    }
//                } else {
//                    Timber.i("사용자 기본정보 받아오기 실패")
//                }
//            }.addOnFailureListener { exception ->
//                Timber.i(exception.toString())
//            }
//        //날짜 초기화
//        today = convertDurationToFormatted(appUser.startDate!!)
//    }


//    //오늘 활동 리스트 가져오기
//    fun fireGetDaily() {
//        val myDailyList = ArrayList<ListData>()
//
//        fireDB.collection("User").document(fireUser?.email.toString()).collection("Project${appUser.projectCount}")
//            .get()
//            .addOnSuccessListener { result  ->
//                if(result != null && result.size() != 0){
//                    for (document in result ) {
//                        val mylist = document.toObject<MyList>()
//                        when (today) {
//                            1 -> myDailyList.add(mylist.day1act)
//                            2 -> myDailyList.add(mylist.day2act)
//                            3 -> myDailyList.add(mylist.day3act)
//                            4 -> myDailyList.add(mylist.day4act)
//                            5 -> myDailyList.add(mylist.day5act)
//                            6 -> myDailyList.add(mylist.day6act)
//                            7 -> myDailyList.add(mylist.day7act)
//                            8 -> myDailyList.add(mylist.day8act)
//                            9 -> myDailyList.add(mylist.day9act)
//                            10 -> myDailyList.add(mylist.day10act)
//                            11 -> myDailyList.add(mylist.day11act)
//                            12 -> myDailyList.add(mylist.day12act)
//                            13 -> myDailyList.add(mylist.day13act)
//                            14 -> myDailyList.add(mylist.day14act)
//                            15 -> myDailyList.add(mylist.day15act)
//                            16 -> myDailyList.add(mylist.day16act)
//                            17 -> myDailyList.add(mylist.day17act)
//                            18 -> myDailyList.add(mylist.day18act)
//                            19 -> myDailyList.add(mylist.day19act)
//                            20 -> myDailyList.add(mylist.day20act)
//                            21 -> myDailyList.add(mylist.day21act)
//                        }
//                    }
//                    for(i in 0 until 5){
//                        listArray[i] = myDailyList[i]
//                    }
//                    setListHolder(listArray)
//                }else{
//                    Timber.i("비어있음")
//                }
//            }.addOnFailureListener { exception ->
//                Timber.i(exception.toString())
//            }
//    }
    //이미 한 sust
    private var mySustList = ArrayList<Int>()
    fun fireGetSust(){
        //지속 가능한 활동 받아오기
        fireDB.collection("User").document(fireUser?.email.toString()).collection("Sustainable")
            .get()
            .addOnSuccessListener { result  ->
                for (document in result ) {
                    mySustList.clear()
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
                    myExtraList.clear()
                    val myextra = document.toObject<MyExtra>()
                    myExtraList.add(myextra.extraID!!)
                    Timber.i( "Extra result: $myExtraList")
                }
            }.addOnFailureListener { exception ->
                Timber.i(exception.toString())
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
