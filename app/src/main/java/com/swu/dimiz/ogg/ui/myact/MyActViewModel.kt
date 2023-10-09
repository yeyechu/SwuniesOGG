package com.swu.dimiz.ogg.ui.myact

import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.contents.listset.listutils.ListData
import com.swu.dimiz.ogg.convertDurationToFormatted
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
    //                                     일반활동 리스트 초기화
    var listArray = ArrayList<ListData>()

    private val _listHolder = MutableLiveData<List<ListData>?>()
    val listHolder: LiveData<List<ListData>?>
        get() = _listHolder

    private fun setListHolder(data: List<ListData>) {
        _listHolder.postValue(data)
    }
    // ───────────────────────────────────────────────────────────────────────────────────
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

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                     firebase 초기화
    private val fireDB = Firebase.firestore
    private val fireUser = Firebase.auth.currentUser

    //todo 오늘 며칠째인지 받아오기
    private var today = 0
    private var appUser = MyCondition()   //사용자 기본 정보 저장

    //사용자 기본 정보
    fun fireInfo(){
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
        //날짜 초기화
        today = convertDurationToFormatted(appUser.startDate!!)
    }


    //오늘 활동 리스트 가져오기
    fun fireGetDaily() {
        val myDailyList = ArrayList<ListData>()

        fireDB.collection("User").document(fireUser?.email.toString()).collection("Project${appUser.projectCount}")
            .get()
            .addOnSuccessListener { result  ->
                if(result != null && result.size() != 0){
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
                }else{
                    Timber.i("비어있음")
                }
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