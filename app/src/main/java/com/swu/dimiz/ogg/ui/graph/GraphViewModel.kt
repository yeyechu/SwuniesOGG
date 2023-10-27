package com.swu.dimiz.ogg.ui.graph

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.contents.listset.listutils.ID_MODIFIER
import com.swu.dimiz.ogg.contents.listset.listutils.NO_TITLE
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesExtra
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesSustainable
import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyAllAct
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyGraph
import kotlinx.coroutines.*
import timber.log.Timber

//─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── 인서님 여기 경고 뜨는 거 신경 써주세요, 하라는 대로 고치면 됩니다 ▲
class GraphViewModel(private val repository: OggRepository) : ViewModel() {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       파이어베이스 변수
    private val fireDB = Firebase.firestore
    private val fireUser = Firebase.auth.currentUser

    var projectCount = 0
    var startDate = 0L

    //──────────────────────────────────────────────────────────────────────────────────────
    //
    //myact
    private val _energyCo2 = MutableLiveData<Float>()
    val energyCo2: LiveData<Float>
        get() = _energyCo2

    private val _consumptionCo2 = MutableLiveData<Float>()
    val consumptionCo2: LiveData<Float>
        get() = _consumptionCo2

    private val _transportCo2 = MutableLiveData<Float>()
    val transportCo2: LiveData<Float>
        get() = _transportCo2

    private val _resourceCo2 = MutableLiveData<Float>()
    val resourceCo2: LiveData<Float>
        get() = _resourceCo2


    private val _co2ActList = MutableLiveData<List<MyAllAct>>()
    val co2ActList: LiveData<List<MyAllAct>>
        get() = _co2ActList

    //certify
    private val _gotMyFeedList = MutableLiveData<List<Feed>>()
    val gotMyFeedList: LiveData<List<Feed>>
        get() = _gotMyFeedList

    private val _sumReactionList = MutableLiveData<List<Int>>()
    val sumReactionList: LiveData<List<Int>>
        get() = _sumReactionList

    private val _myPost = MutableLiveData<MyGraph?>()
    val myPost: LiveData<MyGraph?>
        get() = _myPost


    //special
    private val _extraRank = MutableLiveData<Float>()
    val extraRank: LiveData<Float>
        get() = _extraRank

    //certify

    //special
    private val _graph = MutableLiveData<MyGraph>()
    val graph: LiveData<MyGraph>
        get() = _graph

    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    init {
        Timber.i("created")
        // 인서님,
        // null 허용 안되는 변수들은 전부 여기서 초기화 해주셔야 합니다
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       전체활동 가져오기
    fun getGraph() {
        //_graph.value!!.co21 =
    }

    // ─────────────────────────────────────────────────────────────────────────────────────
    //                                         활동 타이틀
    // 기본적인 형태로 만들었고
    // 인서님이 필요한 대로 수정해서 쓰시면 됩니다
    private fun getTitle(id: Int) {
        uiScope.launch {
            _title.value = when (id / ID_MODIFIER) {
                1 -> getDailyTitleFromRoomDatabase(id).title
                2 -> getSustTitleFromRoomDatabase(id).title
                3 -> getExtraTitleFromRoomDatabase(id).title
                else -> NO_TITLE
            }
        }
    }

    private suspend fun getDailyTitleFromRoomDatabase(id: Int): ActivitiesDaily {
        return withContext(Dispatchers.IO) {
            repository.getActivityById(id)
        }
    }

    private suspend fun getSustTitleFromRoomDatabase(id: Int): ActivitiesSustainable {
        return withContext(Dispatchers.IO) {
            repository.getSust(id)
        }
    }

    private suspend fun getExtraTitleFromRoomDatabase(id: Int): ActivitiesExtra {
        return withContext(Dispatchers.IO) {
            repository.getExtraDate(id)
        }
    }

    fun fireInfo() {
        fireDB.collection("User").document(fireUser?.email.toString())
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val gotUser = snapshot.toObject<MyCondition>()!!
                    projectCount = gotUser.projectCount
                    //0이면 없음 페이지
                    startDate = gotUser.startDate
                    fireGetCategory()
                    fireGetCo2()
                    fireGetReaction()
                    fireGetMostUp()
                    fireGetExtra()
                } else {
                    Timber.i("Current data: null")
                }
            }
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       Daily 가져오기

    //todo 각 함수에 update문 넣기
    private fun fireGetCategory(){
        val docRef = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project$projectCount").document("Entire").collection("AllAct")
        //에너지
        var energyCo2 = 0.0
        var consumptionCo2 = 0.0
        var transportCo2 = 0.0
        var resourceCo2 = 0.0

        docRef.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Timber.i(e)
                return@addSnapshotListener
            }
            for (dc in snapshots!!.documentChanges) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    var act = dc.document.toObject<MyAllAct>()
                    if(act.actCode == "에너지"){
                        energyCo2 += act.allCo2
                    }
                    else if(act.actCode == "소비"){
                        consumptionCo2 += act.allCo2
                    }
                    else if(act.actCode == "이동수단"){
                        transportCo2 += act.allCo2
                    }
                    else if(act.actCode == "자원순환"){
                        resourceCo2 += act.allCo2
                    }
                }
            }
            //각 카테고리별 Co2합
            Timber.i("energyCo2 $energyCo2")
            Timber.i("consumptionCo2 $consumptionCo2")
            Timber.i("transportCo2 $transportCo2")
            Timber.i("resourceCo2 $resourceCo2")
        }
    }

    private fun fireGetCo2(){
        val docRef = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project$projectCount").document("Entire").collection("AllAct")

        var co2ActList = arrayListOf<MyAllAct>()
        docRef.orderBy("allCo2",  Query.Direction.DESCENDING).limit(3)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        var act = dc.document.toObject<MyAllAct>()
                        co2ActList.add(act)  //여기에 123위 순서대로 담겨있음
                    }
                }
                //분리한다면 아래 같음
                val firstId = co2ActList[0].ID
                val secondId = co2ActList[1].ID
                val thirdId = co2ActList[2].ID
                val firstCo2 = co2ActList[0].allCo2
                val secondCo2 = co2ActList[1].allCo2
                val thirdCo2 = co2ActList[2].allCo2

                Timber.i("co2ActList $co2ActList")
            }
    }
    /*private fun fireGetCategoryCo2(){
        val docRef = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project$projectCount").document("Entire").collection("AllAct")

        var energyCo2 = 0.0
        var consumptionCo2 = 0.0
        var transportCo2 = 0.0
        var resourceCo2 = 0.0

        var co2ActList = arrayListOf<Double>()

        docRef.whereLessThan("ID", 20000) //daily만 가져오기
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        var act = dc.document.toObject<MyAllAct>()
                        co2ActList.add(act.allCo2)

                        if (act.actCode == "에너지") {
                            // 에너지 + 소비 + 이동수동 + 자원순환 = 전체
                            // 에너지 / 전체 * 100
                            energyCo2 += act.allCo2.toFloat() * 1000
                        } else if (act.actCode == "소비") {
                            consumptionCo2 += act.allCo2.toFloat() * 1000
                        } else if (act.actCode == "이동수단") {
                            transportCo2 += act.allCo2.toFloat() * 1000
                        } else if (act.actCode == "자원순환") {
                            resourceCo2 += act.allCo2.toFloat() * 1000
                        }
                    }
                }
                //각 카테고리별 Co2합
                Timber.i("energyCo2 $energyCo2")
                Timber.i("consumptionCo2 $consumptionCo2")
                Timber.i("transportCo2 $transportCo2")
                Timber.i("resourceCo2 $resourceCo2")

                // LiveData를 업데이트
                _energyCo2.value = energyCo2.toFloat()
                _consumptionCo2.value = consumptionCo2.toFloat()
                _transportCo2.value = transportCo2.toFloat()
                _resourceCo2.value = resourceCo2.toFloat()

                // 그래프 없을 때 처리
                //분리한다면 아래 같음
                //가장 많은 탄소를 줄인 활동명 3
                co2ActList.sortDescending()
                Timber.i("co2ActList $co2ActList")

                //_co2ActList.value = co2ActList
            }
    }*/

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       전체활동 가져오기
    data class feedReact(var id: String, var reactionSum: Int)

    private var reactionList = arrayListOf<feedReact>()

    private var resultId = ""

    private fun fireGetReaction() {
        reactionList.clear()
        val less = startDate + 21000000
        fireDB.collection("Feed")
            .whereEqualTo("email", fireUser?.email.toString())
            .whereGreaterThan("postTime", startDate)
            .whereLessThan("postTime", less)
            .orderBy("postTime", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val feed = document.toObject<Feed>()
                    feed.id = document.id

                    var reactFun = feed.reactionFun
                    var reactGreat = feed.reactionGreat
                    var reactLike = feed.reactionLike

                    var reaccTotal = reactFun + reactGreat + reactLike

                    reactionList.add(feedReact(feed.id, reaccTotal))
                }
                //순서대로 정렬
                reactionList.sortByDescending { it.reactionSum }

                resultId = reactionList[0].id
                Timber.i("resultId $resultId")
                //todo 이미지 가져오는 쪽에서 firebase 사용해서 값 가져오기
            }
            .addOnFailureListener { exception ->
                Timber.i(exception)
            }
    }

    private fun fireGetMostUp() {
        val docRef = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project$projectCount").document("Entire").collection("AllAct")

        var mostUpList = arrayListOf<Int>()

        docRef.orderBy("upCount", Query.Direction.DESCENDING).limit(3)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        Timber.i("${dc.document.id} => ${dc.document.data}")
                        var act = dc.document.toObject<MyAllAct>()
                        mostUpList.add(act.ID)  //여기에 123위 순서대로 담겨있음
                    }
                }
                //분리한다면 아래 같음
                val upFirstId = mostUpList[0]
                val upSecondId = mostUpList[1]
                val upThirdId = mostUpList[2]

                Timber.i("mostUpList $mostUpList")
            }
    }

    //특별활동 전체 순위
    private fun fireGetExtra(){
        val docRef = fireDB.collection("User")

        var usersExtraList = arrayListOf<Int>()
        var uExtra = 0

        docRef
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        var user = dc.document.toObject<MyCondition>()
                        if(user.email == fireUser?.email.toString()){
                            uExtra = user.extraPost
                        }
                        usersExtraList.add(user.extraPost)  //전체회원 특별 올린 횟수
                    }
                }

                usersExtraList.sortDescending()

                var level = 0
                var size = usersExtraList.size
                for( i in 0 until size){
                    if(uExtra == usersExtraList[i]){
                        level = i
                    }
                }
                var rank = ((size.toDouble() - level.toDouble()) / size.toDouble()) * 100
                Timber.i("level $level")
                Timber.i("size $size")
                Timber.i("rank $rank")
            }
    }




    fun updateEnergyCo2(value: Float) {
        _energyCo2.value = value
    }

    fun updateConsumptionCo2(value: Float) {
        _consumptionCo2.value = value
    }

    fun updateTransportCo2(value: Float) {
        _transportCo2.value = value
    }

    fun updateResourceCo2(value: Float) {
        _resourceCo2.value = value
    }

    fun updateCo2ActList(data: List<MyAllAct>) {
        _co2ActList.value = data
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        Timber.i("destroyed")
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[APPLICATION_KEY] as OggApplication).repository
                GraphViewModel(repository = repository)
            }
        }
    }
}