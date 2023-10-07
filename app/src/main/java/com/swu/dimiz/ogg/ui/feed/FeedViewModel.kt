package com.swu.dimiz.ogg.ui.feed

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.contents.listset.listutils.*
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed
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

    init {
        getFilters()
        onFilterChanged(TOGETHER, true)
        Timber.i("created")
    }

    // ───────────────────────────────────────────────────────────────────────────────────
    //                                          이동
    fun onFeedDetailClicked(feed: Feed) {
        _navigateToSelectedItem.value = feed
    }

    fun onFeedDetailCompleted() {
        _navigateToSelectedItem.value = null
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

    // ───────────────────────────────────────────────────────────────────────────────────
    //                             firebase 피드리스트 받기
    // 필터링은 전체/에너지/소비/이동수단/자원순환 + 내가 올린 글
    // 이렇게 총 6가지이고
    // 필터링만 바꿔서 나의 피드로 들어감
//todo 코드 수정 필요
    /*val fireDB = Firebase.firestore

    fireDB.collection("Feed").addSnapshotListener {
            querySnapshot, FirebaseFIrestoreException ->
        if(querySnapshot!=null){
            for(dc in querySnapshot.documentChanges){
                if(dc.type== DocumentChange.Type.ADDED){
                    var feed= dc.document.toObject<Feed>()
                    feed.id=dc.document.id
                    feedList.add(feed)
                }
            }
            feedAdapter.notifyDataSetChanged()
        }else Timber.i("feed storage 가져오기 오류", FirebaseFIrestoreException)
    }*/

    override fun onCleared() {
        super.onCleared()
        Timber.i("destroyed")
    }
}