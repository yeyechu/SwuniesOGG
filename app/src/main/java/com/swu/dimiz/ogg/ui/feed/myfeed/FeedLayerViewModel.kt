package com.swu.dimiz.ogg.ui.feed.myfeed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyPost
import com.swu.dimiz.ogg.oggdata.MyPostDatabaseDao
import kotlinx.coroutines.*

class FeedLayerViewModel(
    val database: MyPostDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var myPost = MutableLiveData<MyPost?>()
    private val posts = database.getAllPosts()

    init {
        initializePost()
    }

    private fun initializePost() {
        uiScope.launch {
            myPost.value = getMyPostFromDatabase()
        }
    }

    private suspend fun getMyPostFromDatabase(): MyPost? {

        return withContext(Dispatchers.IO) {
            var post = database.getPost()

            if(post?.imageUrl == "") {
                post = null
            }
            post
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}