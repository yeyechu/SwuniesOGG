package com.swu.dimiz.ogg.ui.feed

import android.content.ClipData
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.firestore.DocumentChange
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
         1 -> Timber.i("눌린 버튼 $item")
            2 -> Timber.i("눌린 버튼 $item")
            3 -> Timber.i("눌린 버튼 $item")
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


    //firestore에서 이미지 url을 받아옴
    private val fireDB = Firebase.firestore
    var gotFeed = Feed()
    var gotFeedList = arrayListOf<Feed>()
    fun fireGetFeed(){
        fireDB.collection("Feed").addSnapshotListener {   //실시간 업데이트 가져오기
                querySnapshot, FirebaseFirestoreException ->
            if(querySnapshot!=null){
                for(dc in querySnapshot.documentChanges){
                    if(dc.type== DocumentChange.Type.ADDED){
                        for (document in querySnapshot) {
                            gotFeedList.clear()
                            val feed = document.toObject<Feed>()
                            gotFeed.id = document.id.toLong()
                            gotFeed.imageUrl = feed.imageUrl
                            gotFeed.actCode = feed.actCode
                            gotFeed.actId = feed.actId
                            gotFeedList.add(gotFeed)
                        }
                        _feedList.value = gotFeedList
                        Timber.i(_feedList.value.toString())
                    }
                }
            }
        }
    }
}