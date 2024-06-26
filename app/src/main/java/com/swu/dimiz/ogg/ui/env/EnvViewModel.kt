package com.swu.dimiz.ogg.ui.env

import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.contents.listset.listutils.*
import com.swu.dimiz.ogg.convertLongToDateString
import com.swu.dimiz.ogg.convertToDuration
import com.swu.dimiz.ogg.oggdata.remotedatabase.*
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.collections.ArrayList

class EnvViewModel : ViewModel() {
    //──────────────────────────────────────────────────────────────────────────────────────
    //                                      회원 정보 저장

    // 스탬프
    private var stampList = ArrayList<StampData>()
    private var stampArr = ArrayList<MyStamp>()

    private val _stampHolder = MutableLiveData<List<StampData>?>()
    val stampHolder: LiveData<List<StampData>?>
        get() = _stampHolder

    // Co2
    private val _aimWholeCo2 = MutableLiveData<Float>()

    private val _todayCo2 = MutableLiveData<Float>()
    val todayCo2: LiveData<Float>
        get() = _todayCo2

    private val _untilTodayCo2 = MutableLiveData<Float>()

    private val _co2Holder = MutableLiveData<Float>()
    val co2Holder: LiveData<Float>
        get() = _co2Holder

    private val _leftHolder = MutableLiveData<Float>()
    val leftHolder: LiveData<Float>
        get() = _leftHolder

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                      이동 감지

    private val _navigateToMyEnv = MutableLiveData<Boolean>()
    val navigateToMyEnv: LiveData<Boolean>
        get() = _navigateToMyEnv

    private val _navigateToStart = MutableLiveData<Boolean>()
    val navigateToStart: LiveData<Boolean>
        get() = _navigateToStart

    private val _navigateToStartFromMyAct = MutableLiveData<Boolean>()
    val navigateToStartFromMyAct: LiveData<Boolean>
        get() = _navigateToStartFromMyAct

    private val _navigateToListset = MutableLiveData<Boolean>()
    val navigateToListset: LiveData<Boolean>
        get() = _navigateToListset

    private val _expandLayout = MutableLiveData<Boolean>()
    val expandLayout: LiveData<Boolean>
        get() = _expandLayout

    // ─────────────────────────────────────────────────────────────────────────────────────
    //                                         배지 위치
    private var badgeList = ArrayList<BadgeLocation>()

    // 파이어베이스 배지
    private val _badgeHolder = MutableLiveData<List<BadgeLocation>?>()
    val badgeHolder: LiveData<List<BadgeLocation>?>
        get() = _badgeHolder

    private val _badgeSize = MutableLiveData<Int>()
    val badgeSize: LiveData<Int>
        get() = _badgeSize

    private val _setToast = MutableLiveData<Int>()
    val setToast: LiveData<Int>
        get() = _setToast

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                   파이어베이스 변수
    private val _userCondition = MutableLiveData<MyCondition>()
    val userCondition: LiveData<MyCondition>
        get() = _userCondition

    private val fireDB = Firebase.firestore
    private val fireUser = Firebase.auth

    private var today: Int = 0
    private var appUser = MyCondition()

    init {
        setCo2(AIMCO2_ONE)
        _todayCo2.value = FLOAT_ZERO
        _co2Holder.value = FLOAT_ZERO
        _untilTodayCo2.value = FLOAT_ZERO

        fireInfo()

        stampArr.clear()
        stampInitialize()
        setStampHolder(stampList)

        Timber.i("ViewModel created")
        Timber.i("────────────────────── 날짜 변환 확인용 로그 ──────────────────────")
        Timber.i("${convertLongToDateString(1696954754160)} 이후 : ${convertToDuration(1696954754160)}일 경과")
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                        UI용 매핑
    val layerVisible = userCondition.map {
        it.startDate == 0L
    }

    val date = userCondition.map {
        convertToDuration(it.startDate)
    }

    val progressWhole = co2Holder.map {
        it.div(_aimWholeCo2.value!!).times(100).toInt()
    }

    val progressDaily = todayCo2.map {
        it.div(_userCondition.value!!.aim).times(100).toInt()
    }

    val progressEnv = co2Holder.map {
        it.div(_untilTodayCo2.value!!).times(100).toInt()
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                    탄소량 출력 및 계산
    fun setCo2(co2: Float) {
        _aimWholeCo2.value = co2 * CO2_WHOLE
    }

    fun setUntilTodayCo2(co2: Float, date: Int?) {
        date?.let {
            _untilTodayCo2.value = co2 * it
        }
    }

    fun leftCo2() {
        _leftHolder.value = _aimWholeCo2.value!!.minus(_co2Holder.value!!)
    }

    private fun plusCo2All(data: Float) {
        _co2Holder.value = _co2Holder.value!!.plus(data)
    }

    fun onMakeToast(case: Int) {
        _setToast.value = case
    }

    fun onToastCompleted() {
        _setToast.value = 0
    }

    private fun setBadgeSize() {
        _badgeSize.value = _badgeSize.value!!.plus(1)
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                      스탬프 초기화
    private fun setStampHolder(item: List<StampData>) {
        _stampHolder.postValue(item)
    }

    private fun stampInitialize() {
        stampList.clear()

        for (i in 1..DATE_WHOLE) {
            stampList.add(StampData(i, 0f, 0))
        }
    }

    private fun setStamp() {
        val tempDate = date.value!!
        _co2Holder.value = FLOAT_ZERO

        if (tempDate in 1..DATE_WHOLE) {

            if (tempDate == 1) {
                stampList[0] = StampData(tempDate, _todayCo2.value!!, 1)
                plusCo2All(stampList[0].sNumber)
            } else {
                for (i in 0..tempDate - 2) {
                    stampList[i] =
                        StampData(sId = i + 1, sNumber = stampArr[i].dayCo2.toFloat(), today = 2)
                    Timber.i("지난 스탬프 리스트 초기화 : ${stampList[i]}")
                    plusCo2All(stampList[i].sNumber)
                }
                stampList[tempDate - 1] = StampData(tempDate, _todayCo2.value!!, 1)
                Timber.i("오늘 스탬프 리스트 초기화 : ${stampList[tempDate - 1]}")
                plusCo2All(stampList[tempDate - 1].sNumber)
            }
            setStampHolder(stampList)

        } else {
            // ───────────────────────────────────────────────────────────────────────────
            //                                   스탬프 리셋
            resetCondition()

            _todayCo2.value = FLOAT_ZERO
            _co2Holder.value = FLOAT_ZERO
            _untilTodayCo2.value = FLOAT_ZERO

            stampInitialize()
            setStampHolder(stampList)
        }
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                   배지 위치 가져오기
    fun initLocationFromFirebase() {
        badgeList.clear()
        _badgeSize.value = 0

        fireDB.collection("User").document(fireUser.currentUser?.email.toString())
            .collection("Badge")
            .whereNotEqualTo("getDate", null)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Timber.i("${document.id} => ${document.data}")
                    val gotBadge = document.toObject<MyBadge>()
                    if(gotBadge.getDate != null) {
                        setBadgeSize()
                        if(gotBadge.valueX != 0.0) {
                            badgeList.add(BadgeLocation(gotBadge.badgeID!!, gotBadge.valueX.toFloat(), gotBadge.valueY.toFloat()))
                        }
                    }
                }
                _badgeHolder.value = badgeList
            }
            .addOnFailureListener { exception ->
                Timber.i(exception)
            }
    }

    fun setLocationList(list: List<BadgeLocation>) {
        _badgeHolder.value = list
        onMakeToast(1)
    }

    fun countStampsForBadge(): Int {
        var count = 0

        _stampHolder.value!!.forEach {
            if(it.today != 0) {
                if((_userCondition.value!!.aim - it.sNumber) <= 0f) {
                    count++
                } else {
                    count = 0
                }
            }
        }
        return count
    }

    // ──────────────────────────────────────────────────────────────────────────────────────
    //                                         이동 감지

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
    }

    fun onNavigatedToStart() {
        _navigateToStart.value = false
    }

    fun onStartClickedFromMyAct() {
        _navigateToStartFromMyAct.value = true
    }

    fun onNavigatedToStartFromMyAct() {
        _navigateToStartFromMyAct.value = false
    }

    fun onListsetClicked() {
        _navigateToListset.value = true
    }

    fun onNavigatedToListset() {
        _navigateToListset.value = false
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                   파이어베이스 함수
    private fun resetCondition() = viewModelScope.launch {
        val docRef = fireDB.collection("User").document(fireUser.currentUser!!.email.toString())

        // 플젝 초기화할때 이전 그래프 저장
        if(_userCondition.value!!.projectCount != 0 && _userCondition.value!!.startDate != 0L){
            val projectCount = _userCondition.value!!.projectCount

            fireGetCategory(projectCount)
            fireGetCo2(projectCount)
            fireGetReaction(projectCount)
            fireGetMostPost(projectCount)
            fireGetExtra(projectCount)
        }

        docRef
            .update("startDate", 0L)
            .addOnSuccessListener { Timber.i("DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Timber.i("Error updating document", e) }
        docRef
            .update("aim", 0f)
            .addOnSuccessListener { Timber.i("DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Timber.i("Error updating document", e) }
    }

    //──────────────────────────────────────────────────────────────────────────────────────
    //                                       Daily 가져오기
    private fun fireGetCategory(num: Int) = viewModelScope.launch {
        val docRef = fireDB.collection("User").document(fireUser.currentUser?.email.toString())
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

            //sever Graph 업데이트
            fireDB.collection("User").document(fireUser.currentUser?.email.toString())
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

    private fun fireGetCo2(num: Int) = viewModelScope.launch {
        val docRef = fireDB.collection("User").document(fireUser.currentUser?.email.toString())
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
                    // mostCo2List 사이즈만큼만 올라가게
                    for( i in 0 until mostCo2List.size){
                        fireDB.collection("User").document(fireUser.currentUser?.email.toString())
                            .collection("Project$num").document("Graph")
                            .update("nameCo2${i + 1}" , mostCo2List[i].ID)
                            .addOnSuccessListener { }
                            .addOnFailureListener { exception ->Timber.i(exception) }

                        fireDB.collection("User").document(fireUser.currentUser?.email.toString())
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
    private fun fireGetReaction(num: Int) = viewModelScope.launch  {
        val reactionList = arrayListOf<FeedReact>()
        reactionList.clear()

        fireDB.collection("Feed")
            .whereEqualTo("email", fireUser.currentUser?.email.toString())
            .whereGreaterThan("postTime", userCondition.value!!.startDate)
            .orderBy("postTime", Query.Direction.DESCENDING)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }

                for (doc in value!!) {
                    val feed = doc.toObject<Feed>()
                    feed.id = doc.id

                    reactionList.add(
                        FeedReact(
                            feed.id,
                            feed.reactionFun + feed.reactionGreat + feed.reactionLike,
                            feed.actTitle
                        )
                    )
                }
                reactionList.sortByDescending { it.reactionSum }

                if (reactionList.size != 0) {
                    fireDB.collection("Feed").document(reactionList[0].id)
                        .addSnapshotListener { snapshot, exception ->
                            if (exception != null) {
                                Timber.i(exception)
                                return@addSnapshotListener
                            }

                            if (snapshot != null && snapshot.exists()) {
                                val gotFeed = snapshot.toObject<Feed>()

                                gotFeed?.let {

                                    fireDB.collection("User").document(fireUser.currentUser?.email.toString())
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
                                        .addOnFailureListener { e -> Timber.i(e) }
                                }
                            } else {
                                Timber.i("Current data: null")
                            }
                        }
                }
            }
    }

    private fun fireGetMostPost(num: Int) = viewModelScope.launch  {
        val docRef = fireDB.collection("User").document(fireUser.currentUser?.email.toString())
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
                //sever Graph 업데이트
                if(mostPostList.size != 0) {
                    for( i in 0 until mostPostList.size){
                        fireDB.collection("User").document(fireUser.currentUser?.email.toString())
                            .collection("Project$num").document("Graph")
                            .update("post${i + 1}", mostPostList[i].id)
                            .addOnSuccessListener { }
                            .addOnFailureListener { exception ->Timber.i(exception) }
                        fireDB.collection("User").document(fireUser.currentUser?.email.toString())
                            .collection("Project$num").document("Graph")
                            .update("postCount${i + 1}", mostPostList[i].count)
                            .addOnSuccessListener { }
                            .addOnFailureListener { exception ->Timber.i(exception) }
                    }
                }
            }
    }

    //특별활동 전체 순위
    private fun fireGetExtra(num: Int) = viewModelScope.launch  {
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
                if(user.email == fireUser.currentUser?.email.toString()){
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

                //sever Graph 업데이트
                fireDB.collection("User").document(fireUser.currentUser?.email.toString())
                    .collection("Project$num").document("Graph")
                    .update("extraRank", rank)
                    .addOnSuccessListener { }
                    .addOnFailureListener { exception ->Timber.i(exception) }
            }
        }
    }

    private fun fireInfo() = viewModelScope.launch {
        //사용자 기본 정보
        fireDB.collection("User").document(fireUser.currentUser?.email.toString())
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    appUser = snapshot.toObject<MyCondition>()!!
                    today = convertToDuration(appUser.startDate)

                    Timber.i("오늘 $today")
                    _userCondition.value = appUser

                    Timber.i("${_userCondition.value}")
                } else {
                    Timber.i("Current data: null")
                }
            }
    }

    fun fireGetStamp() = viewModelScope.launch {
        _userCondition.value?.email?.let {
            if(_userCondition.value?.startDate != 0L) {
                val tempList = arrayListOf<MyStamp>()
                tempList.clear()

                fireDB.collection("User").document(fireUser.currentUser?.email.toString())
                    .collection("Project${_userCondition.value?.projectCount}").document("Entire")
                    .collection("Stamp")
                    .orderBy("day")
                    .addSnapshotListener { value, e ->
                        if (e != null) {
                            Timber.i("listen:error $e")
                            return@addSnapshotListener
                        }
                        for (doc in value!!) {
                            val stamp = doc.toObject<MyStamp>()
                            tempList.add(stamp)

                            if (stamp.day == date.value) {
                                _todayCo2.value = stamp.dayCo2.toFloat()
                                Timber.i("fireGetStamp todayCo2 초기화 : ${_todayCo2.value}")
                            }
                        }
                        stampArr = tempList
                        Timber.i("스탬프 어레이 초기화: $stampArr")
                        setStamp()
                    }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("ViewModel destroyed")
    }
}