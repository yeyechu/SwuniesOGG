package com.swu.dimiz.ogg

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.swu.dimiz.ogg.oggdata.MyPost
import com.swu.dimiz.ogg.oggdata.MyPostDatabaseDao
import com.swu.dimiz.ogg.oggdata.OggDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class OggDatabaseTest {

    private lateinit var postDao: MyPostDatabaseDao
    private lateinit var db: OggDatabase

    @Before
    fun createDB() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, OggDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        postDao = db.myPostDatabaseDao
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
}