package com.swu.dimiz.ogg.ui.feed

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.contents.listset.listutils.*
import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class FeedViewModel : ViewModel() {

    private var currentJob: Job? = null
    private val fireDB = Firebase.firestore

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
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Timber.i("listen:error $e")
                    return@addSnapshotListener
                }
                gotFeedList.clear()
                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val feed = dc.document.toObject<Feed>()
                        feed.id = dc.document.id
                        gotFeedList.add(feed)
                    }
                }

                _feedList.value = gotFeedList
                _filteredList.value = gotFeedList
                Timber.i("Feed 초기화")
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
