package com.swu.dimiz.ogg

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.swu.dimiz.ogg.oggdata.MyPostDatabaseDao
import com.swu.dimiz.ogg.oggdata.OggDatabase
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDailyDatabaseDao
import com.swu.dimiz.ogg.oggdata.localdatabase.ListDatabaseDao
import com.swu.dimiz.ogg.oggdata.remotedatabase.ListSet
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class OggDatabaseTest {

    private lateinit var postDao: MyPostDatabaseDao
    private lateinit var actDao: ActivitiesDailyDatabaseDao
    private lateinit var listDao: ListDatabaseDao

    private lateinit var db: OggDatabase


    @Before
    fun createDB() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, OggDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        postDao = db.myPostDatabaseDao
        actDao = db.dailyDatabaseDao
        listDao = db.listSetDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetPost() {
        val post = MyPost()
        postDao.insert(post)
        val posted = postDao.getPost()
        assertEquals(posted?.reactionLike, 0)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetDaily() {
        val daily = ActivitiesDaily()
        actDao.insert(daily)
        val day = actDao.getDaily()
        assertEquals(day?.freq, 0)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetList() {
        val list = ListSet()
        listDao.insert(list)
        val listSet = listDao.getItem()
        assertEquals(list?.listId, 1)
    }
}