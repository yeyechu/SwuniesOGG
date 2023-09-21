package com.swu.dimiz.ogg.oggdata

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.swu.dimiz.ogg.oggdata.localdatabase.*
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyPost
import kotlinx.coroutines.flow.Flow

class OggRepository(private val database: OggDatabase) {

    val getAlldata : Flow<List<ActivitiesDaily>> = database.dailyDatabaseDao.getAllDailys()
    val getPost : LiveData<List<MyPost>> = database.myPostDatabaseDao.getAllPosts()
    val getAllBadges : Flow<List<Badges>> = database.badgesDatabaseDao.getAllItem()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(post: MyPost) {
        database.myPostDatabaseDao.insert(post)
    }
}