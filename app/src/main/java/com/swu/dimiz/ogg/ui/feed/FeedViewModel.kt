package com.swu.dimiz.ogg.ui.feed

import android.content.Context
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.GridLayoutManager
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.OggApplication
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.*
import com.swu.dimiz.ogg.contents.listset.listutils.*
import com.swu.dimiz.ogg.oggdata.OggRepository
import com.swu.dimiz.ogg.oggdata.remotedatabase.Feed
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class FeedViewModel(private val repository: OggRepository) : ViewModel()  {    //, FeedAdapter.OnItemClickListener

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

    init {
        getFilters()
        onFilterChanged(TOGETHER, true)
        Timber.i("created")
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

    private lateinit var feedAdapter:FeedAdapter
    private fun onFilterUpdated(filter: String) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {

            try {
                //_feedList.value = repository.getFiltered(filter)
            } catch (e: IOException) {
                _feedList.value = listOf()
            }
        }

        // ───────────────────────────────────────────────────────────────────────────────────
        //                             firebase 피드리스트 받기
//todo 코드 수정 필요
        /*val fireDB = Firebase.firestore

        feedAdapter =FeedAdapter(this ,_feedList)

        binding.feedListGrid.layoutManager= GridLayoutManager(this, 3)
        binding.feedListGrid.adapter=feedAdapter

        feedAdapter.onItemClickListener = this

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
    }
//todo 코드 수정 필요
   /* override fun onItemClick(feed: Feed) {
         val bundle = bundleOf("id" to feed.id)
         view?.findNavController()
             ?.navigate(R.id.action_navigation_feed_to_feedDetailFragment, bundle)
    }*/

    fun onFilterChanged(filter: String, isChecked: Boolean) {
        if (isChecked) {
            onFilterUpdated(filter)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("destroyed")
    }

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as OggApplication).repository
                FeedViewModel(repository = repository)
            }
        }
    }
}