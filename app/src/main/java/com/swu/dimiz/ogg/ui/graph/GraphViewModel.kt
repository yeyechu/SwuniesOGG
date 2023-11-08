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

    var projectCount = 0
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

        _energyCo2.value = FLOAT_ZERO
        _consumptionCo2.value = FLOAT_ZERO
        _transportCo2.value = FLOAT_ZERO
        _resourceCo2.value = FLOAT_ZERO

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

                    projectCount = gotUser.projectCount
                    getProjectSize(gotUser.projectCount)

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
                val act = doc.toObject<MyAllAct>()
                when (act.actCode) {
                    ENERGY -> energyCo2 += act.allCo2.toFloat()
                    CONSUME -> consumptionCo2 += act.allCo2.toFloat()
                    TRANSPORT -> transportCo2 += act.allCo2.toFloat()
                    RECYCLE -> resourceCo2 += act.allCo2.toFloat()
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
                //분리한다면 아래 같음

                val firstCo2 = co2ActList[0].allCo2
                val secondCo2 = co2ActList[1].allCo2
                val thirdCo2 = co2ActList[2].allCo2
                val firstId :Int = co2ActList[0].ID
                val secondId :Int = co2ActList[1].ID
                val thirdId :Int = co2ActList[2].ID


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

    // todo 빙하면적 해초지

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       최고 반응 피드
    private var reactionList = arrayListOf<FeedReact>()

    private fun fireGetReaction() {
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
                                    .collection("Project$projectCount").document("Graph")
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

    private fun fireGetMostUp() {
        val docRef = fireDB.collection("User").document(fireUser?.email.toString())
            .collection("Project$projectCount").document("Entire").collection("AllAct")

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

                //분리한다면 아래 같음
                val upFirstId :Int = mostUpList[0]
                val upSecondId :Int = mostUpList[1]
                val upThirdId :Int = mostUpList[2]

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
            val rank = ((size.toDouble() - level.toDouble()) / size.toDouble()) * 100 - 99
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