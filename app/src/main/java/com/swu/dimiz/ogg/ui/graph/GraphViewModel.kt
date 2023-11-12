package com.swu.dimiz.ogg.ui.graph

import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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

class GraphViewModel(private val repository: OggRepository) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       파이어베이스 변수
    private val fireDB = Firebase.firestore
    private val email = OggApplication.auth.currentUser!!.email.toString()

    // ───────────────────────────────────────────────────────────────────────────────────
    //                               뷰페이저 관리용 : GraphFragment
    private val _userCondition = MutableLiveData<MyCondition>()
    val userCondition: LiveData<MyCondition>
        get() = _userCondition

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

    private val _noFeed = MutableLiveData<Boolean>()
    val noFeed: LiveData<Boolean>
        get() = _noFeed

    private val _co2ForCategory = MutableLiveData<List<Float>>()
    val co2ForCategory: LiveData<List<Float>>
        get() = _co2ForCategory

    private val co2List = ArrayList<Float>()

    private val _co2Sum = MutableLiveData<Float>()
    private val co2Sum: LiveData<Float>
        get() = _co2Sum

    private val _mostCo2List = MutableLiveData<List<MyAllAct>?>()
    val mostCo2List: LiveData<List<MyAllAct>?>
        get() = _mostCo2List

    private val _mostPostList = MutableLiveData<List<PostCount>?>()
    val mostPostList: LiveData<List<PostCount>?>
        get() = _mostPostList

    //special
    private val _rank = MutableLiveData<Float>()
    val rank: LiveData<Float>
        get() = _rank

    private val _graph = MutableLiveData<MyGraph>()
    val graph: LiveData<MyGraph>
        get() = _graph

    private val _titlesPost = MutableLiveData<List<String>>()
    val titlesPost: LiveData<List<String>>
        get() = _titlesPost

    private val _titlesCo2 = MutableLiveData<List<String>>()
    val titlesCo2: LiveData<List<String>>
        get() = _titlesCo2

    init {
        Timber.i("created")
        _currentPage.value = ID_MODIFIER
        _userCondition.value = MyCondition()
        _rank.value = FLOAT_ZERO
    }

    // ─────────────────────────────────────────────────────────────────────────────────────
    //                                     XML파일 UI 매핑
    val layoutVisible = userCondition.map {
        _userCondition.value!!.projectCount == 0
    }

    val leftButtonEnable = currentPage.map {
        it != ID_MODIFIER && it > 1
    }

    val rightButtonEnable = currentPage.map {
        it < _userCondition.value!!.projectCount
    }

    val glacierCo2 = co2Sum.map {
        it * 0.3.toFloat()
    }

    val seaweedCo2 = co2Sum.map {
        it * 2.025.toFloat()
    }

    // ─────────────────────────────────────────────────────────────────────────────────────
    //                                     GraphFragment
    fun setCurrentPage(num: Int) {
        _currentPage.value = num
        Timber.i("현재 페이지: ${_currentPage.value}회차")
    }

    // ─────────────────────────────────────────────────────────────────────────────────────
    //                                      GraphLayer
    fun getCo2List(): List<MyAllAct> {
        return _mostCo2List.value!!
    }

    private fun getCo2Percent(co2: Float, total: Float): Float {
        return (co2 / total * 100)
    }

    private fun setCo2List(list: List<Float>) {
        _co2ForCategory.value = list
    }

    val postProgress2 = mostPostList.map {
        if(it?.size!! >= 2) {
            (100f / _mostPostList.value!![0].count * _mostPostList.value!![1].count).toInt()
        } else {
            0
        }
    }

    val postProgress3 = mostPostList.map {
        if(it?.size!! == 3) {
            (100f / _mostPostList.value!![0].count * _mostPostList.value!![2].count).toInt()
        } else {
            0
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────────────
    //                                         활동 타이틀
    private fun getTitlesCo2(list: List<MyAllAct>) {
        val titleList = ArrayList<String>()
        titleList.clear()

        uiScope.launch {
            list.forEach {
                when (it.ID / ID_MODIFIER) {
                    1 -> titleList.add(getDailyTitleFromRoomDatabase(it.ID).title)
                    2 -> titleList.add(getSustTitleFromRoomDatabase(it.ID).title)
                    3 -> titleList.add(getExtraTitleFromRoomDatabase(it.ID).title)
                }
            }
            setTitleCo2(titleList)
        }
    }

    private fun getTitlesPost(list: List<PostCount>) {
        val titleList = ArrayList<String>()
        titleList.clear()

        uiScope.launch {
            list.forEach {
                when (it.id / ID_MODIFIER) {
                    1 -> titleList.add(getDailyTitleFromRoomDatabase(it.id).title)
                    2 -> titleList.add(getSustTitleFromRoomDatabase(it.id).title)
                    3 -> titleList.add(getExtraTitleFromRoomDatabase(it.id).title)
                }
            }
            setTitlePost(titleList)
        }
    }

    private fun setTitlePost(list: List<String>) {
        _titlesPost.value = list
    }
    private fun setTitleCo2(list: List<String>) {
        _titlesCo2.value = list
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
        fireDB.collection("User").document(email)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val gotUser = snapshot.toObject<MyCondition>()!!
                    _userCondition.value = gotUser

                } else {
                    Timber.i("Current data: null")
                }
            }
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       Daily 가져오기
    fun fireGetCategory(num: Int) {
        val docRef = fireDB.collection("User").document(email)
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

            //각 카테고리별 Co2합
            Timber.i("energyCo2 $energyCo2")
            Timber.i("consumptionCo2 $consumptionCo2")
            Timber.i("transportCo2 $transportCo2")
            Timber.i("resourceCo2 $resourceCo2")

            //sever Graph 업데이트
            fireDB.collection("User").document(email)
                .collection("Project$num").document("Graph")
                .update(
                    mapOf(
                        "energy" to energyCo2,
                        "consumption" to consumptionCo2,
                        "transport" to transportCo2,
                        "resource" to resourceCo2
                    ),
                )
                .addOnSuccessListener { }
                .addOnFailureListener { exception ->Timber.i(exception) }
        }
    }

    fun fireGetCo2(num: Int){
        val docRef = fireDB.collection("User").document(email)
            .collection("Project$num").document("Entire").collection("AllAct")

        val mostCo2List = arrayListOf<MyAllAct>()
        mostCo2List.clear()

        docRef.orderBy("allCo2",  Query.Direction.DESCENDING).limit(3)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }

                for (doc in value!!) {
                    val act = doc.toObject<MyAllAct>()

                    if(act.allCo2 != 0.0) {
                        mostCo2List.add(act)  //여기에 123위 순서대로 담겨있음
                    }
                }
                if(mostCo2List.size != 0) {

                    Timber.i("가장 많은 탄소량 활동 리스트: $mostCo2List")

                    // mostCo2List 사이즈만큼만 올라가게
                    for( i in 0 until mostCo2List.size){
                        fireDB.collection("User").document(email)
                            .collection("Project$num").document("Graph")
                            .update("nameCo2${i + 1}" , mostCo2List[i].ID)
                            .addOnSuccessListener { }
                            .addOnFailureListener { exception ->Timber.i(exception) }

                        fireDB.collection("User").document(email)
                            .collection("Project$num").document("Graph")
                            .update("co2Sum${i + 1}", mostCo2List[i].allCo2)
                            .addOnSuccessListener { }
                            .addOnFailureListener { exception ->Timber.i(exception) }
                    }
                }
            }
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       최고 반응 피드
    fun fireGetReaction(num: Int) {
        val reactionList = arrayListOf<FeedReact>()
        reactionList.clear()

        fireDB.collection("Feed")
            .whereEqualTo("email", email)
            .whereGreaterThan("postTime", userCondition.value!!.startDate)
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
                reactionList.sortByDescending { it.reactionSum }

                //sever Graph 업데이트
                if(reactionList.size != 0){
                    fireDB.collection("Feed").document(reactionList[0].id)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val gotFeed = document.toObject<Feed>()

                                gotFeed?.let {

                                    fireDB.collection("User").document(email)
                                        .collection("Project$num").document("Graph")
                                        .update(
                                            mapOf(
                                                "reactionURI" to gotFeed.imageUrl,
                                                "reactionTitle" to gotFeed.actTitle,
                                                "funny" to gotFeed.reactionFun,
                                                "great" to gotFeed.reactionGreat,
                                                "like" to gotFeed.reactionLike
                                            ),
                                        ).addOnSuccessListener { }
                                        .addOnFailureListener { e ->Timber.i(e) }
                                }
                            } else {
                                Timber.i("No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Timber.i(exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Timber.i(exception)
            }
    }

    fun fireGetMostPost(num: Int) {
        val docRef = fireDB.collection("User").document(email)
            .collection("Project$num").document("Entire").collection("AllAct")

        val mostPostList = arrayListOf<PostCount>()
        mostPostList.clear()

        docRef.orderBy("upCount", Query.Direction.DESCENDING).limit(3)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }

                for (doc in value!!) {
                    val act = doc.toObject<MyAllAct>()
                    if(act.upCount != 0) {
                        mostPostList.add(PostCount(act.ID, act.upCount))
                    }
                }

                Timber.i("가장 많이 인증한 활동 리스트: $mostPostList")

                //sever Graph 업데이트
                if(mostPostList.size != 0) {
                    for( i in 0 until mostPostList.size){
                        fireDB.collection("User").document(email)
                            .collection("Project$num").document("Graph")
                            .update("post${i + 1}", mostPostList[i].id)
                            .addOnSuccessListener { }
                            .addOnFailureListener { exception ->Timber.i(exception) }
                        fireDB.collection("User").document(email)
                            .collection("Project$num").document("Graph")
                            .update("postCount${i + 1}", mostPostList[i].count)
                            .addOnSuccessListener { }
                            .addOnFailureListener { exception ->Timber.i(exception) }
                    }
                }
            }
    }

    //특별활동 전체 순위
    fun fireGetExtra(num: Int) {
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
                if(user.email == email){
                    uExtra = user.extraPost
                }
                if(uExtra != 0) {
                    usersExtraList.add(user.extraPost)  //전체회원 특별 올린 횟수
                }
            }

            if(uExtra != 0) {
                usersExtraList.sortDescending()

                var level = 0
                val size = usersExtraList.size
                for (i in 0 until size) {
                    if (uExtra == usersExtraList[i]) {
                        level = i
                    }
                }
                val rank = (level.toFloat() / size.toFloat()) * 100
                Timber.i("level $level")
                Timber.i("size $size")
                Timber.i("rank $rank")

                //sever Graph 업데이트
                fireDB.collection("User").document(email)
                    .collection("Project$num").document("Graph")
                    .update("extraRank", rank)
                    .addOnSuccessListener { }
                    .addOnFailureListener { exception ->Timber.i(exception) }
            }
        }
    }

    //이전 프로젝트 불러오기
    fun fireGetBeforPorject(projectNum : Int){   //몇회차 이전, 이후 필요한지 넣어주기

        val mostCo2List = arrayListOf<MyAllAct>()
        val mostPostList = arrayListOf<PostCount>()
        mostCo2List.clear()
        mostPostList.clear()

        fireDB.collection("User").document(email)
            .collection("Project$projectNum").document("Graph")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Timber.i("DocumentSnapshot data: ${document.data}")
                    val gotGraph = document.toObject<MyGraph>()

                    gotGraph?.let {
                        // ───────────────────────────────── 카테고리별 탄소량 ─────────────────────────────────
                        _co2Sum.value = gotGraph.energy.toFloat() + gotGraph.consumption.toFloat() + gotGraph.transport.toFloat() + gotGraph.resource.toFloat()

                        co2List.clear()
                        co2List.add(getCo2Percent(gotGraph.energy.toFloat(), _co2Sum.value!!.toFloat()))
                        co2List.add(getCo2Percent(gotGraph.consumption.toFloat(), _co2Sum.value!!.toFloat()))
                        co2List.add(getCo2Percent(gotGraph.transport.toFloat(), _co2Sum.value!!.toFloat()))
                        co2List.add(getCo2Percent(gotGraph.resource.toFloat(), _co2Sum.value!!.toFloat()))

                        setCo2List(co2List)
                        Timber.i("co2List: $co2List")
                        // ───────────────────────────────── 가장 많은 탄소량 ─────────────────────────────────

                        if(gotGraph.nameCo21 != 0) {
                            mostCo2List.add(MyAllAct(gotGraph.nameCo21, "", 0, gotGraph.co2Sum1))
                        }
                        if(gotGraph.nameCo22 != 0) {
                            mostCo2List.add(MyAllAct(gotGraph.nameCo22, "", 0, gotGraph.co2Sum2))
                        }
                        if(gotGraph.nameCo23 != 0) {
                            mostCo2List.add(MyAllAct(gotGraph.nameCo23, "", 0, gotGraph.co2Sum3))
                        }

                        if(mostCo2List.size != 0) {
                            getTitlesCo2(mostCo2List)
                            Timber.i("graph 가장 많은 탄소량 활동 리스트: $mostCo2List")
                            _mostCo2List.value = mostCo2List
                        } else {
                            _mostCo2List.value = listOf()
                        }

                        // ───────────────────────────────── 가장 많은 리액션 ─────────────────────────────────
                        _noFeed.value = (gotGraph.like + gotGraph.funny + gotGraph.great) == 0
                        _feed.value = Feed(
                            "",
                            gotGraph.reactionTitle,
                            "",
                            0L,
                            0,
                            "",
                            gotGraph.reactionURI.toString(),
                            gotGraph.like,
                            gotGraph.funny,
                            gotGraph.great,
                            0
                        )
                        // ───────────────────────────────── 가장 많은 인증 활동 ─────────────────────────────────
                        if(gotGraph.post1 != 0) {
                            mostPostList.add(PostCount(gotGraph.post1, gotGraph.postCount1))
                        }
                        if(gotGraph.post2 != 0) {
                            mostPostList.add(PostCount(gotGraph.post2, gotGraph.postCount2))
                        }
                        if(gotGraph.post3 != 0) {
                            mostPostList.add(PostCount(gotGraph.post3, gotGraph.postCount3))
                        }

                        if(mostPostList.size != 0) {
                            getTitlesPost(mostPostList)
                            _mostPostList.value = mostPostList
                            Timber.i("graph 가장 많이 인증한 활동 리스트: $mostPostList")
                        } else {
                            _mostPostList.value = listOf()
                        }

                    // ───────────────────────────────── 특별활동 ─────────────────────────────────

                        if(userCondition.value!!.extraPost == 0) {
                            _rank.value = 100f
                        } else {
                            _rank.value = gotGraph.extraRank

                            if(_rank.value == 0f) {
                                _rank.value = 0.1f
                            }
                        }
                    }

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