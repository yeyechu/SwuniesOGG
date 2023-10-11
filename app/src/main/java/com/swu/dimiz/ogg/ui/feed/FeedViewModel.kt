package com.swu.dimiz.ogg.ui.feed

import android.content.ClipData
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.contents.listset.listutils.*
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed
import com.swu.dimiz.ogg.ui.feed.myfeed.bindImage
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class FeedViewModel : ViewModel()  {

    private var currentJob: Job? = null

    private val fireDB = Firebase.firestore
    private val fireUser = Firebase.auth.currentUser

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                         필터 적용
    private val category = mutableListOf(TOGETHER, ENERGY, CONSUME, TRANSPORT, RECYCLE)

    // 전체 피드리스트
    private val _feedList = MutableLiveData<List<Feed>>()
    val feedList: LiveData<List<Feed>>
        get() = _feedList

    private val _feedId = MutableLiveData<Feed?>()
    val feedId: LiveData<Feed?>
        get() = _feedId

    val layoutVisible = feedList.map {
        it.isNotEmpty()
    }

    // 필터링된 피드리스트
    private val _filteredList = MutableLiveData<List<Feed>>()
    val filteredList: LiveData<List<Feed>>
        get() = _filteredList

    private val _activityFilter = MutableLiveData<List<String>>()
    val activityFilter: LiveData<List<String>>
        get() = _activityFilter

    private val _navigateToSelectedItem = MutableLiveData<Feed?>()
    val navigateToSelectedItem: LiveData<Feed?>
        get() = _navigateToSelectedItem

    private val _navigateToReport = MutableLiveData<Feed?>()
    val navigateToReport: LiveData<Feed?>
        get() = _navigateToReport

    init {
        getFilters()
        onFilterChanged(TOGETHER, true)
        Timber.i("created")
    }

    fun onreactionClicked(item: Int) {
        when(item) {
            1 -> fireDB.collection("Feed").document(_feedId.value?.id.toString())
                .update("reactionLike", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("like 반응 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            2 -> fireDB.collection("Feed").document(_feedId.value?.id.toString())
                .update("reactionFun", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("Fun 반응 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
            3 -> fireDB.collection("Feed").document(_feedId.value?.id.toString())
                .update("reactionGreat", FieldValue.increment(1))
                .addOnSuccessListener { Timber.i("Great 반응 올리기 완료") }
                .addOnFailureListener { e -> Timber.i( e ) }
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

            try {
                //_feedList.value = repository.getFiltered(filter)
            } catch (e: IOException) {
                _feedList.value = listOf()
            }
        }
    }
    fun onFilterChanged(filter: String, isChecked: Boolean) {
        if (isChecked) {
            onFilterUpdated(filter)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("destroyed")
    }


    // ───────────────────────────────────────────────────────────────────────────────────
    //                             firebase 피드리스트 받기
    // 필터링은 전체/에너지/소비/이동수단/자원순환 + 내가 올린 글
    // 이렇게 총 6가지이고
    // 필터링만 바꿔서 나의 피드로 들어감

    fun fireGetFeed() {
        var gotFeedList = arrayListOf<Feed>()

        fireDB.collection("Feed")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var gotFeed = Feed()
                    val feed = document.toObject<Feed>()
                    gotFeed.id = document.id
                    gotFeed.actTitle = feed.actTitle
                    gotFeed.imageUrl = feed.imageUrl
                    gotFeed.actCode = feed.actCode
                    gotFeed.actId = feed.actId
                    gotFeedList.add(gotFeed)
                }
                Timber.i(gotFeedList.toString())
                _feedList.value = gotFeedList
            }.addOnFailureListener { exception ->
                Timber.i(exception.toString())
            }
    }
}