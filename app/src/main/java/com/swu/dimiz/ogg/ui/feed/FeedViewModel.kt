package com.swu.dimiz.ogg.ui.feed

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.contents.listset.listutils.*
import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyBadge
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyReaction
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class FeedViewModel : ViewModel() {

    private var currentJob: Job? = null
    private val fireDB = Firebase.firestore
    private val fireUser = Firebase.auth.currentUser
    private val userEmail = fireUser?.email.toString()

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                         필터 적용
    private val category = mutableListOf(TOGETHER, ENERGY, CONSUME, TRANSPORT, RECYCLE)

    private val _feedList = MutableLiveData<List<Feed>?>()
    val feedList: LiveData<List<Feed>?>
        get() = _feedList

    private val _filteredList = MutableLiveData<List<Feed>>()
    val filteredList: LiveData<List<Feed>>
        get() = _filteredList

    private val _myList = MutableLiveData<List<Feed>>()
    val myList: LiveData<List<Feed>>
        get() = _myList

    private val _activityFilter = MutableLiveData<List<String>>()
    val activityFilter: LiveData<List<String>>
        get() = _activityFilter

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                        피드 디테일

    private val _feedId = MutableLiveData<Feed?>()
    val feedId: LiveData<Feed?>
        get() = _feedId

    private val _navigateToSelectedItem = MutableLiveData<Feed?>()
    val navigateToSelectedItem: LiveData<Feed?>
        get() = _navigateToSelectedItem

    private val _navigateToReport = MutableLiveData<Feed?>()
    val navigateToReport: LiveData<Feed?>
        get() = _navigateToReport

    private val _makeToast = MutableLiveData<Boolean>()
    val makeToast: LiveData<Boolean>
        get() = _makeToast

    init {
        getFilters()
        onFilterChanged(TOGETHER, true)
        Timber.i("created")
    }

    val layoutVisible = filteredList.map {
        it.isNotEmpty()
    }

    fun onreactionClicked(item: Int) {
        if(_feedId.value!!.email != OggApplication.auth.currentUser!!.email) {
            fireDB.collection("User").document(userEmail)
                .collection("Reation")
                .whereEqualTo("feedId", _feedId.value?.id.toString())
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Timber.i("이미 반응 남김")
                        //todo 이미 남김 알려주는 처리
                    }
                    if(result.isEmpty){
                        val react = MyReaction(_feedId.value?.id.toString())
                        fireDB.collection("User").document(userEmail)
                            .collection("Reation").document(_feedId.value?.id.toString())
                            .set(react)
                            .addOnSuccessListener { Timber.i("MyReaction 업데이트 완료") }
                            .addOnFailureListener { e -> Timber.i(e) }

                        //배지 카운트 업
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40001")
                            .update("count", FieldValue.increment(1))
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40002")
                            .update("count", FieldValue.increment(1))
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40003")
                            .update("count", FieldValue.increment(1))

                        //다른 사용자 피드에 내가 몇개 반응
                        fireDB.collection("User").document(userEmail)
                            .collection("Badge").document("40001")
                            .addSnapshotListener { snapshot, e ->
                                if (e != null) {
                                    Timber.i(e)
                                    return@addSnapshotListener
                                }
                                if (snapshot != null && snapshot.exists()) {
                                    val gotBadge = snapshot.toObject<MyBadge>()

                                    if(gotBadge!!.count == 10 && gotBadge.getDate == null){
                                        val getDate = System.currentTimeMillis()
                                        fireDB.collection("User").document(userEmail)
                                            .collection("Badge").document("40001")
                                            .update("getDate", getDate)
                                            .addOnSuccessListener { Timber.i("40001 획득 완료") }
                                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                                    }
                                    else  if(gotBadge!!.count == 100 && gotBadge.getDate == null){
                                        val getDate = System.currentTimeMillis()
                                        fireDB.collection("User").document(userEmail)
                                            .collection("Badge").document("40002")
                                            .update("getDate", getDate)
                                            .addOnSuccessListener { Timber.i("40002 획득 완료") }
                                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                                    }
                                    if(gotBadge!!.count == 500 && gotBadge.getDate == null){
                                        val getDate = System.currentTimeMillis()
                                        fireDB.collection("User").document(userEmail)
                                            .collection("Badge").document("40003")
                                            .update("getDate", getDate)
                                            .addOnSuccessListener { Timber.i("40003 획득 완료") }
                                            .addOnFailureListener { exeption -> Timber.i(exeption) }
                                    }
                                } else {
                                    Timber.i("Current data: null")
                                }
                            }

                        //반응 받은게 총 몇개인지
                        //반응 버튼 눌렀을때 타사용자(게시물) 반응이 10개넘으면  개시물 주인의(이메일) 카운트 올리기
                        fireDB.collection("Feed").document(_feedId.value?.id.toString())
                            .addSnapshotListener { snapshot, e ->
                                if (e != null) {
                                    Timber.i(e)
                                    return@addSnapshotListener
                                }

                                if (snapshot != null && snapshot.exists()) {
                                    val gotFeed = snapshot.toObject<Feed>()
                                    if (gotFeed != null) {
                                        if(gotFeed.reactionLike == 10){
                                            fireDB.collection("User").document(gotFeed.email)
                                                .collection("Badge").document("40013")
                                                .update("count", FieldValue.increment(1))
                                        }
                                        else if(gotFeed.reactionGreat == 10){
                                            fireDB.collection("User").document(gotFeed.email)
                                                .collection("Badge").document("40012")
                                                .update("count", FieldValue.increment(1))
                                        }
                                        else if(gotFeed.reactionFun == 10){
                                            fireDB.collection("User").document(gotFeed.email)
                                                .collection("Badge").document("40011")
                                                .update("count", FieldValue.increment(1))
                                        }

                                        //카운트가 5가 된게 있으면 개시물 주인의(이메일) 배지 획득
                                        fireDB.collection("User").document(gotFeed.email)
                                            .collection("Badge")
                                            .whereEqualTo("badgeID", "40011")
                                            .whereEqualTo("badgeID", "40012")
                                            .whereEqualTo("badgeID", "40013")
                                            .addSnapshotListener { value, e ->
                                                if (e != null) {
                                                    Timber.i(e)
                                                    return@addSnapshotListener
                                                }

                                                for (doc in value!!) {
                                                    val gotBadge = doc.toObject<MyBadge>()

                                                    if(gotBadge.badgeID == 40011 && gotBadge.getDate == null){
                                                        if(gotBadge.count == 5){
                                                            val getDate = System.currentTimeMillis()
                                                            fireDB.collection("User").document(userEmail)
                                                                .collection("Badge").document("40011")
                                                                .update("getDate", getDate)
                                                                .addOnSuccessListener { Timber.i("40011 획득 완료") }
                                                                .addOnFailureListener { exeption -> Timber.i(exeption) }
                                                        }
                                                    }
                                                    else if(gotBadge.badgeID == 40012 && gotBadge.getDate == null){
                                                        if(gotBadge.count == 5){
                                                            val getDate = System.currentTimeMillis()
                                                            fireDB.collection("User").document(userEmail)
                                                                .collection("Badge").document("40012")
                                                                .update("getDate", getDate)
                                                                .addOnSuccessListener { Timber.i("40012 획득 완료") }
                                                                .addOnFailureListener { exeption -> Timber.i(exeption) }
                                                        }
                                                    }
                                                    else if(gotBadge.badgeID == 40013 && gotBadge.getDate == null){
                                                        if(gotBadge.count == 5){
                                                            val getDate = System.currentTimeMillis()
                                                            fireDB.collection("User").document(userEmail)
                                                                .collection("Badge").document("40013")
                                                                .update("getDate", getDate)
                                                                .addOnSuccessListener { Timber.i("40013 획득 완료") }
                                                                .addOnFailureListener { exeption -> Timber.i(exeption) }
                                                        }
                                                    }
                                                }
                                            }
                                    }
                                } else {
                                    Timber.i("Current data: null")
                                }
                            }

                        when (item) {
                            1 -> fireDB.collection("Feed").document(_feedId.value?.id.toString())
                                .update("reactionLike", FieldValue.increment(1))
                                .addOnSuccessListener { Timber.i("like 반응 올리기 완료") }
                                .addOnFailureListener { e -> Timber.i(e) }

                            2 -> fireDB.collection("Feed").document(_feedId.value?.id.toString())
                                .update("reactionFun", FieldValue.increment(1))
                                .addOnSuccessListener { Timber.i("Fun 반응 올리기 완료") }
                                .addOnFailureListener { e -> Timber.i(e) }

                            3 -> fireDB.collection("Feed").document(_feedId.value?.id.toString())
                                .update("reactionGreat", FieldValue.increment(1))
                                .addOnSuccessListener { Timber.i("Great 반응 올리기 완료") }
                                .addOnFailureListener { e -> Timber.i(e) }
                        }
                        fireGetFeed()
                    }
                }
                .addOnFailureListener { exception ->
                    Timber.i(exception)
                }
        } else {
            onYourFeed()
        }
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                          이동
    fun onFeedDetailClicked(feed: Feed) {
        _navigateToSelectedItem.value = feed
        _feedId.value = feed
    }

    fun onFeedDetailCompleted() {
        _navigateToSelectedItem.value = null
    }

    fun onReportClicked(feed: Feed) {
        _navigateToReport.value = feed
    }

    fun onReportCompleted() {
        _navigateToReport.value = null
    }

    private fun onYourFeed() {
        _makeToast.value = true
    }

    fun onYourFeedCompleted() {
        _makeToast.value = false
    }

    fun noClick() {

    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                          필터
    private fun getFilters() {
        category.let {
            if (it != _activityFilter.value) {
                _activityFilter.value = it
            }
        }
    }

    private fun onFilterUpdated(filter: String) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {

            if(_feedList.value != null) {
                when(filter) {
                    TOGETHER -> _filteredList.value = _feedList.value
                    else -> _filteredList.value = _feedList.value!!.filter { it.actCode == filter }
                }
            } else {
                _filteredList.value = listOf()
            }
        }
    }

    fun onFilterChanged(filter: String, isChecked: Boolean) {
        if (isChecked) {
            onFilterUpdated(filter)
        }
    }

    fun setMyFeedList() = viewModelScope.launch {
        try {
            fireGetMyFeed()
            //_myList.value = _feedList.value!!.filter { it.email == OggApplication.auth.currentUser!!.email }
        } catch (e: Exception) {
            _myList.value = listOf()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("destroyed")
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                             firebase 피드리스트 받기
    fun fireGetFeed() {
        val gotFeedList = arrayListOf<Feed>()

        fireDB.collection("Feed")
            .orderBy("postTime",  Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                gotFeedList.clear()
                for (document in documents) {
                    val feed = document.toObject<Feed>()
                    feed.id = document.id
                    gotFeedList.add(feed)
                }
                _feedList.value = gotFeedList
                _filteredList.value = gotFeedList
                Timber.i("Feed 초기화")
            }
            .addOnFailureListener { exception ->
                Timber.i(exception)
            }
    }

    private fun fireGetMyFeed(){
        val fireUser = Firebase.auth.currentUser
        val gotMyFeedList = arrayListOf<Feed>()

        fireDB.collection("Feed")
            .orderBy("postTime",  Query.Direction.DESCENDING)
            .whereEqualTo("email", fireUser?.email.toString())
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Timber.i(e)
                    return@addSnapshotListener
                }
                gotMyFeedList.clear()
                for (doc in value!!) {
                    val feed = doc.toObject<Feed>()
                    feed.id = doc.id
                    gotMyFeedList.add(feed)
                }
                _myList.value = gotMyFeedList
            }
    }


}
