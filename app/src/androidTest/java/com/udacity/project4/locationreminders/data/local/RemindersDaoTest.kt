package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminder() = runBlockingTest {
        database.reminderDao().saveReminder(ReminderDTO("Test Title","Test Description","Test Loction", 0.0,0.0))

        var checkInsertStatus = database.reminderDao().getReminders()

        assertThat(checkInsertStatus.isNotEmpty(), `is`(true))

    }

    @Test
    fun deleteAllReminders() = runBlockingTest {

        database.reminderDao().saveReminder(ReminderDTO("Test Title","Test Description","Test Loction", 0.0,0.0))

        database.reminderDao().deleteAllReminders()

        var isEmptyDB = database.reminderDao().getReminders()

        assertThat(isEmptyDB.isEmpty(), `is`(true))


    }

    @Test
    fun getReminderById() = runBlockingTest {

        var addNewReminder = ReminderDTO("Test Title","Test Description","Test Loction", 0.0,0.0)

        database.reminderDao().saveReminder(addNewReminder)

        var getReminderStatus = database.reminderDao().getReminderById(addNewReminder.id)

        assertThat(getReminderStatus as ReminderDTO, notNullValue())
        assertThat(getReminderStatus.id, `is`(addNewReminder.id))
        assertThat(getReminderStatus.title, `is`(addNewReminder.title))
        assertThat(getReminderStatus.description, `is`(addNewReminder.description))
        assertThat(getReminderStatus.location, `is`(addNewReminder.location))
        assertThat(getReminderStatus.latitude, `is`(addNewReminder.latitude))
        assertThat(getReminderStatus.longitude, `is`(addNewReminder.longitude))


    }

}