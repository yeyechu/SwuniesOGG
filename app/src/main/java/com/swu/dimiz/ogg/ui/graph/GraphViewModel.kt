package com.swu.dimiz.ogg.ui.graph

import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.ktx.auth
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
import kotlin.math.roundToInt

//todo 플젝 시작하고 아무것도 안올렸을때 오류남
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
    var layoutVisible: Boolean = true

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                   필요한 데이터 초기화

    // 프로젝트 카운트 데이터
    private val projectCountLiveData = MutableLiveData<Int>()

    private val _fireInfoget = MutableLiveData<Int?>()
    val fireInfoget: LiveData<Int?>
        get() = _fireInfoget


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
    private val _funnycnt = MutableLiveData<Int>()
    val funnycnt: LiveData<Int>
        get() = _funnycnt

    private val _greatcnt = MutableLiveData<Int>()
    val greatcnt: LiveData<Int>
        get() = _greatcnt

    private val _likecnt = MutableLiveData<Int>()
    val likecnt: LiveData<Int>
        get() = _likecnt

    private val _reactiontitle = MutableLiveData<String?>()
    val reactiontitle: LiveData<String?>
        get() = _reactiontitle


    private val _mostUpList = MutableLiveData<List<Int>>()
    val mostUpList: LiveData<List<Int>>
        get() = _mostUpList


    //special
    private val _rank = MutableLiveData<Float>()
    val rank: LiveData<Float>
        get() = _rank


    private val _graph = MutableLiveData<MyGraph>()
    val graph: LiveData<MyGraph>
        get() = _graph

    private val _titles = MutableLiveData<List<String>>()
    val titles: LiveData<List<String>>
        get() = _titles

    private val _titlesMost = MutableLiveData<List<String>>()
    val titlesMost: LiveData<List<String>>
        get() = _titlesMost

    private val _projcnt = MutableLiveData<Int>()
    val projcnt: LiveData<Int>
        get() = _projcnt


    init {
        Timber.i("created")
//        projectCountLiveData.value = 0


        _energyCo2.value = 0.0f
        _consumptionCo2.value = 0.0f
        _transportCo2.value = 0.0f
        _resourceCo2.value = 0.0f

        _co2ActList.value = emptyList()

        _funnycnt.value = 0
        _greatcnt.value = 0
        _likecnt.value = 0
        _reactiontitle.value = null

        _mostUpList.value = emptyList()

        _rank.value = 0.0f

    }

    fun getProjectCountLiveData(): LiveData<Int> = projectCountLiveData

    fun fetchFirebaseData(newCount: Int) {
        projectCount = newCount
    }


    fun co2Act(): List<MyAllAct> {
        return _co2ActList.value!!
    }



    // ─────────────────────────────────────────────────────────────────────────────────────
    //                                         활동 타이틀

    private fun getTitle(list: List<MyAllAct>) {
        val titleList = ArrayList<String>()

        uiScope.launch {
            list.forEach {
                when (it.ID / ID_MODIFIER) {
                    1 -> titleList.add(getDailyTitleFromRoomDatabase(it.ID).title)
                    2 -> titleList.add(getSustTitleFromRoomDatabase(it.ID).title)
                    3 -> titleList.add(getExtraTitleFromRoomDatabase(it.ID).title)
                }
            }
            setTitleList(titleList)
        }
    }

    private fun getTitleMost(list: List<Int>) {
        val titleList = ArrayList<String>()

        uiScope.launch {
            list.forEach {
                when (it / ID_MODIFIER) {
                    1 -> titleList.add(getDailyTitleFromRoomDatabase(it).title)
                    2 -> titleList.add(getSustTitleFromRoomDatabase(it).title)
                    3 -> titleList.add(getExtraTitleFromRoomDatabase(it).title)
                }
            }
            setTitleListMost(titleList)
        }
    }

    fun setTitleList(list: List<String>) {
        _titles.value = list
    }
    fun setTitleListMost(list: List<String>) {
        _titlesMost.value = list
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


    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       전체활동 가져오기
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
                    fetchFirebaseData(gotUser.projectCount)

                    // 0이면 없음 페이지
                    layoutVisible = projectCount == 0

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
    private fun fireGetCategory(){
        val docRef = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project$projectCount").document("Entire").collection("AllAct")
        //에너지
        var energyCo2 = 0.0
        var consumptionCo2 = 0.0
        var transportCo2 = 0.0
        var resourceCo2 = 0.0

        docRef.addSnapshotListener { value, e ->
            if (e != null) {
                Timber.i(e)
                return@addSnapshotListener
            }
            for (doc in value!!) {
                var act = doc.toObject<MyAllAct>()
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


            val totalSum = energyCo2+ consumptionCo2 + transportCo2 + resourceCo2

            _energyCo2.value = ((energyCo2 / totalSum) * 100.0f).toFloat()
            _consumptionCo2.value = ((consumptionCo2 / totalSum) * 100.0f).toFloat()
            _transportCo2.value = ((transportCo2/ totalSum) * 100.0f).toFloat()
            _resourceCo2.value = ((resourceCo2 / totalSum) * 100.0f).toFloat()

            //각 카테고리별 Co2합
            Timber.i("energyCo2 $energyCo2")
            Timber.i("consumptionCo2 $consumptionCo2")
            Timber.i("transportCo2 $transportCo2")
            Timber.i("resourceCo2 $resourceCo2")


            //sever Graph 업데이트
            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Project$projectCount").document("Graph")
                .update(
                    mapOf(
                        "energy" to energyCo2,
                        "consumption" to consumptionCo2,
                        "transport" to transportCo2,
                        "resource" to resourceCo2
                    ),
                )
        }
    }

    private fun fireGetCo2(){
        val docRef = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project$projectCount").document("Entire").collection("AllAct")

        var co2ActList = arrayListOf<MyAllAct>()
        docRef.orderBy("allCo2",  Query.Direction.DESCENDING).limit(3)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }
                for (doc in value!!) {
                    var act = doc.toObject<MyAllAct>()
                    co2ActList.add(act)  //여기에 123위 순서대로 담겨있음
                }
                getTitle(co2ActList)
                //분리한다면 아래 같음
                val firstId = co2ActList[0].ID
                val secondId = co2ActList[1].ID
                val thirdId = co2ActList[2].ID
                val firstCo2 = co2ActList[0].allCo2
                val secondCo2 = co2ActList[1].allCo2
                val thirdCo2 = co2ActList[2].allCo2

                Timber.i("co2ActList $co2ActList")
                _co2ActList.value = co2ActList

                //sever Graph 업데이트
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Project$projectCount").document("Graph")
                    .update(
                        mapOf(
                            "nameCo21" to firstId,
                            "nameCo22" to secondId,
                            "nameCo23" to thirdId,
                            "co2Sum1" to firstCo2,
                            "co2Sum2" to secondCo2,
                            "co2Sum3" to thirdCo2
                        ),
                    )
            }
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       전체활동 가져오기
    data class feedReact(var id: String, var reactionSum: Int, var title : String)

    private var reactionList = arrayListOf<feedReact>()

    private var resultId = ""

    var funny = 0
    var great = 0
    var like = 0


    private fun fireGetReaction() {
        reactionList.clear()
        val less = startDate + 21000000
        fireDB.collection("Feed")
            .whereEqualTo("email", fireUser?.email.toString())
            .whereGreaterThan("postTime", startDate)
            //.whereLessThan("postTime", less)
            .orderBy("postTime", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val feedList = mutableListOf<feedReact>()
                for (document in documents) {
                    val feed = document.toObject<Feed>()
                    feed.id = document.id

                    val reactFun = feed.reactionFun
                    val reactGreat = feed.reactionGreat
                    val reactLike = feed.reactionLike

                    var reaccTotal = reactFun + reactGreat + reactLike
                    reactionList.add(feedReact(feed.id, reaccTotal, feed.actTitle))
                }
                //순서대로 정렬
                reactionList.sortByDescending { it.reactionSum }

                resultId = reactionList[0].id
                var resultTitle = reactionList[0].title
                Timber.i("resultId $resultId")

                //sever Graph 업데이트
                fireDB.collection("Feed").document(reactionList[0].id)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            var gotFeed = document.toObject<Feed>()

                            funny = gotFeed!!.reactionFun
                            great = gotFeed!!.reactionGreat
                            like = gotFeed!!.reactionLike

                            // LiveData를 통해 UI에 값을 업데이트
                            _funnycnt.value = funny
                            _greatcnt.value = great
                            _likecnt.value = like
                            _reactiontitle.value = resultTitle
//                            _reactionuri.value = reactionURI

                            fireDB.collection("User").document(fireUser?.email.toString())
                                .collection("Project$projectCount").document("Graph")
                                .update(
                                    mapOf(
                                        "reactionURI" to reactionList[0].id,
                                        "funny" to funny,
                                        "great" to great,
                                        "like" to like
                                    ),
                                )
                        } else {
                            Timber.i("No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Timber.i(exception)
                    }



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
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }
                for (doc in value!!) {
                    Timber.i("${doc.id} => ${doc.data}")
                    var act = doc.toObject<MyAllAct>()
                    mostUpList.add(act.ID)  //여기에 123위 순서대로 담겨있음
                }
                getTitleMost(mostUpList)

                //분리한다면 아래 같음
                val upFirstId = mostUpList[0]
                val upSecondId = mostUpList[1]
                val upThirdId = mostUpList[2]

                _mostUpList.value = mostUpList

                Timber.i("mostUpList $mostUpList")

                //sever Graph 업데이트

                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Project$projectCount").document("Graph")
                    .update(
                        mapOf(
                            "post1" to upFirstId,
                            "post2" to upSecondId,
                            "post3" to upThirdId
                        ),
                    )

            }
    }

    //특별활동 전체 순위
    private fun fireGetExtra(){
        val docRef = fireDB.collection("User")

        var usersExtraList = arrayListOf<Int>()
        var uExtra = 0

        docRef.addSnapshotListener { value, e ->
            if (e != null) {
                Timber.i(e)
                return@addSnapshotListener
            }
            for (doc in value!!) {
                var user = doc.toObject<MyCondition>()
                if(user.email == fireUser?.email.toString()){
                    uExtra = user.extraPost
                }
                usersExtraList.add(user.extraPost)  //전체회원 특별 올린 횟수
            }

            usersExtraList.sortDescending()

            var level = 0
            var size = usersExtraList.size
            for( i in 0 until size){
                if(uExtra == usersExtraList[i]){
                    level = i
                }
            }
            var rank = ((size.toDouble() - level.toDouble()) / size.toDouble()) * 100 - 99
            Timber.i("level $level")
            Timber.i("size $size")
            Timber.i("rank $rank")

            val roundedRank: Float = (rank * 10.0f).roundToInt() / 10.0f
            _rank.value = roundedRank

            //sever Graph 업데이트
            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Project$projectCount").document("Graph")
                .update(
                    mapOf(
                        "extraRank" to rank
                    ),
                )
        }
    }

    //새로운 플젝시작할때 저장하고 지우고 하는것도 괜찮을것 같음

    //이전 프로젝트 불러오기
    private fun fireGetBeforPorject(projectNum : Int){   //몇회차 이전, 이후 필요한지 넣어주기
        fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project$projectNum").document("Graph")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Timber.i("DocumentSnapshot data: ${document.data}")
                    val gotGraph = document.toObject<MyGraph>()
                } else {
                    Timber.i("No such document")
                }
            }
            .addOnFailureListener { exception -> Timber.i(exception) }
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