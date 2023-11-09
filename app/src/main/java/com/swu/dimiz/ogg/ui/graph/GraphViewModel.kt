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
import com.swu.dimiz.ogg.contents.listset.listutils.*
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

class GraphViewModel(private val repository: OggRepository) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       파이어베이스 변수
    private val fireDB = Firebase.firestore
    private val fireUser = Firebase.auth.currentUser

    var startDate = 0L

    // ───────────────────────────────────────────────────────────────────────────────────
    //                               뷰페이저 관리용 : GraphFragment
    private val _projectSize = MutableLiveData<Int>()
    val projectSize: LiveData<Int>
        get() = _projectSize

    private val _currentPage = MutableLiveData<Int>()
    val currentPage: LiveData<Int>
        get() = _currentPage

    private val _leftPager = MutableLiveData<Boolean>()
    val leftPager: LiveData<Boolean>
        get() = _leftPager

    private val _rightPager = MutableLiveData<Boolean>()
    val rightPager: LiveData<Boolean>
        get() = _rightPager

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                 그래프 데이터 : GraphLayer
    private val _feed = MutableLiveData<Feed>()
    val feed: LiveData<Feed>
        get() = _feed

    private val _co2ForCategory = MutableLiveData<List<Float>>()
    val co2ForCategory: LiveData<List<Float>>
        get() = _co2ForCategory

    private val co2List = ArrayList<Float>()

    private val _co2Sum = MutableLiveData<Float>()
    private val co2Sum: LiveData<Float>
        get() = _co2Sum

    private val _co2ActList = MutableLiveData<List<MyAllAct>>()
    val co2ActList: LiveData<List<MyAllAct>>
        get() = _co2ActList

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

    init {
        Timber.i("created")

        _currentPage.value = ID_MODIFIER
        _projectSize.value = INTEGER_ZERO

        _rank.value = FLOAT_ZERO
    }

    // ─────────────────────────────────────────────────────────────────────────────────────
    //                                     XML파일 UI 매핑
    val layoutVisible = projectSize.map {
        projectSize.value == 0
    }

    val leftButtonEnable = currentPage.map {
        it != ID_MODIFIER && it > 1
    }

    val rightButtonEnable = currentPage.map {
        it < _projectSize.value!!
    }

    val glacierCo2 = co2Sum.map {
        it * 0.3.toFloat()
    }

    val seaweedCo2 = co2Sum.map {
        it * 2.025.toFloat()
    }

    // ─────────────────────────────────────────────────────────────────────────────────────
    //                                     GraphFragment
    private fun getProjectSize(num: Int) {
        _projectSize.value = num
    }

    fun setCurrentPage(num: Int) {
        _currentPage.value = num + 1
        Timber.i("현재 페이지: ${_currentPage.value}")
    }

    // ─────────────────────────────────────────────────────────────────────────────────────
    //                                      GraphLayer
    fun co2Act(): List<MyAllAct> {
        return _co2ActList.value!!
    }

    private fun getCo2Percent(co2: Float, total: Float): Float {
        return (co2 / total * 100)
    }

    private fun setCo2List(list: List<Float>) {
        _co2ForCategory.value = list
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

    private fun setTitleList(list: List<String>) {
        _titles.value = list
    }
    private fun setTitleListMost(list: List<String>) {
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
    // ─────────────────────────────────────────────────────────────────────────────────────
    //                                         인터랙션 감지
    fun onLeftButtonClicked() {
        _leftPager.value = true
    }

    fun onLeftCompleted() {
        _leftPager.value = false
    }
    fun onRightButtonClicked() {
        _rightPager.value = true
    }

    fun onRightCompleted() {
        _rightPager.value = false
    }

    // ─────────────────────────────────────────────────────────────────────────────────────
    fun fireInfo() {
        fireDB.collection("User").document(fireUser?.email.toString())
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val gotUser = snapshot.toObject<MyCondition>()!!

                    getProjectSize(gotUser.projectCount)

                    startDate = gotUser.startDate

                } else {
                    Timber.i("Current data: null")
                }
            }
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       Daily 가져오기
    fun fireGetCategory(num: Int) {
        val docRef = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project$num").document("Entire").collection("AllAct")
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
                val act = doc.toObject<MyAllAct>()
                when (act.actCode) {
                    ENERGY -> energyCo2 += act.allCo2
                    CONSUME -> consumptionCo2 += act.allCo2
                    TRANSPORT -> transportCo2 += act.allCo2
                    RECYCLE -> resourceCo2 += act.allCo2
                }
            }

            val totalSum = energyCo2+ consumptionCo2 + transportCo2 + resourceCo2
            _co2Sum.value = totalSum.toFloat()

            co2List.clear()
            co2List.add(getCo2Percent(energyCo2.toFloat(), totalSum.toFloat()))
            co2List.add(getCo2Percent(consumptionCo2.toFloat(), totalSum.toFloat()))
            co2List.add(getCo2Percent(transportCo2.toFloat(), totalSum.toFloat()))
            co2List.add(getCo2Percent(resourceCo2.toFloat(), totalSum.toFloat()))

            setCo2List(co2List)
            Timber.i("co2List: $co2List")

            //각 카테고리별 Co2합
            Timber.i("energyCo2 $energyCo2")
            Timber.i("consumptionCo2 $consumptionCo2")
            Timber.i("transportCo2 $transportCo2")
            Timber.i("resourceCo2 $resourceCo2")

            //sever Graph 업데이트
            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Project$num").document("Graph")
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

    fun fireGetCo2(num: Int){
        val docRef = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project$num").document("Entire").collection("AllAct")

        val co2ActList = arrayListOf<MyAllAct>()
        docRef.orderBy("allCo2",  Query.Direction.DESCENDING).limit(3)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }
                for (doc in value!!) {
                    val act = doc.toObject<MyAllAct>()
                    co2ActList.add(act)  //여기에 123위 순서대로 담겨있음
                }
                getTitle(co2ActList)
                Timber.i("co2ActList $co2ActList")
                _co2ActList.value = co2ActList

                //sever Graph 업데이트
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Project$num").document("Graph")
                    .update(
                        mapOf(
                            "nameCo21" to co2ActList[0].allCo2,
                            "nameCo22" to co2ActList[1].allCo2,
                            "nameCo23" to co2ActList[2].allCo2,
                            "co2Sum1" to co2ActList[0].allCo2,
                            "co2Sum2" to co2ActList[1].allCo2,
                            "co2Sum3" to co2ActList[2].allCo2
                        ),
                    )
            }
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       최고 반응 피드
    private var reactionList = arrayListOf<FeedReact>()

    fun fireGetReaction(num: Int) {
        reactionList.clear()

        fireDB.collection("Feed")
            .whereEqualTo("email", fireUser?.email.toString())
            .whereGreaterThan("postTime", startDate)
            .orderBy("postTime", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    val feed = document.toObject<Feed>()
                    feed.id = document.id

                    reactionList.add(FeedReact(
                        feed.id,
                        feed.reactionFun + feed.reactionGreat + feed.reactionLike,
                        feed.actTitle))
                }

                //순서대로 정렬
                reactionList.sortByDescending { it.reactionSum }

                //sever Graph 업데이트
                fireDB.collection("Feed").document(reactionList[0].id)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val gotFeed = document.toObject<Feed>()

                            gotFeed?.let {
                                _feed.value = it

                                fireDB.collection("User").document(fireUser?.email.toString())
                                    .collection("Project$num").document("Graph")
                                    .update(
                                        mapOf(
                                            "reactionURI" to gotFeed.imageUrl,
                                            "reactionTitle" to gotFeed.actTitle,
                                            "funny" to gotFeed.reactionFun,
                                            "great" to gotFeed.reactionGreat,
                                            "like" to gotFeed.reactionLike
                                        ),
                                    )
                            }
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

    fun fireGetMostUp(num: Int) {
        val docRef = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project$num").document("Entire").collection("AllAct")

        val mostUpList = arrayListOf<Int>()

        docRef.orderBy("upCount", Query.Direction.DESCENDING).limit(3)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }
                for (doc in value!!) {
                    Timber.i("${doc.id} => ${doc.data}")
                    val act = doc.toObject<MyAllAct>()
                    mostUpList.add(act.ID)  //여기에 123위 순서대로 담겨있음
                }
                getTitleMost(mostUpList)

                _mostUpList.value = mostUpList
                Timber.i("mostUpList $mostUpList")

                //sever Graph 업데이트
                fireDB.collection("User").document(fireUser?.email.toString())
                    .collection("Project$num").document("Graph")
                    .update(
                        mapOf(
                            "post1" to mostUpList[0],
                            "post2" to mostUpList[1],
                            "post3" to mostUpList[2]
                        ),
                    )
            }
    }

    //특별활동 전체 순위
    fun fireGetExtra(num: Int){
        val docRef = fireDB.collection("User")

        val usersExtraList = arrayListOf<Int>()
        var uExtra = 0

        docRef.addSnapshotListener { value, e ->
            if (e != null) {
                Timber.i(e)
                return@addSnapshotListener
            }
            for (doc in value!!) {
                val user = doc.toObject<MyCondition>()
                if(user.email == fireUser?.email.toString()){
                    uExtra = user.extraPost
                }
                usersExtraList.add(user.extraPost)  //전체회원 특별 올린 횟수
            }

            usersExtraList.sortDescending()

            var level = 0
            val size = usersExtraList.size
            for( i in 0 until size){
                if(uExtra == usersExtraList[i]){
                    level = i
                }
            }
            val rank = (level.toFloat() / size.toFloat()) * 100
            Timber.i("level $level")
            Timber.i("size $size")
            Timber.i("rank $rank")

            val roundedRank: Float = (rank * 10.0f).roundToInt() / 10.0f
            _rank.value = roundedRank

            //sever Graph 업데이트
            fireDB.collection("User").document(fireUser?.email.toString())
                .collection("Project$num").document("Graph")
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