package com.swu.dimiz.ogg

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.swu.dimiz.ogg.oggdata.OggDatabase
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDaily
import com.swu.dimiz.ogg.oggdata.localdatabase.ActivitiesDailyDatabaseDao
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class OggDatabaseTest {

    private lateinit var actDao: ActivitiesDailyDatabaseDao
    private lateinit var db: OggDatabase


    @Before
    fun createDB() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, OggDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        actDao = db.dailyDatabaseDao

    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        db.close()
    }

//    @Test
//    @Throws(Exception::class)
//    fun insertAndGetList() {
//        val item = ActivitiesDaily(0, "", "", 0.0f, 1, 3, 1, 0, null, null, 1)
//        actDao.insert(item)
//        val act = actDao.getToday()
//        assertEquals(act.value!!.size, 1)
//    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetDaily() {
        val daily = ActivitiesDaily()
        actDao.insert(daily)
        val day = actDao.getDaily()
        assertEquals(day?.freq, 0)
    }

}