package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var database: RemindersDatabase
    private lateinit var dao: RemindersDao
    private lateinit var localRepository: RemindersLocalRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = database.reminderDao()
        localRepository = RemindersLocalRepository(
            dao,
            Dispatchers.Main
        )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    fun saveReminder_And_Then_GetReminder_By_Id() = runBlocking{
        val newReminder = ReminderDTO("Test Title","Test Description", "Test Location",0.0,0.0)
        localRepository.saveReminder(newReminder)

        val retriveReminder = localRepository.getReminder(newReminder.id) as Result.Success<ReminderDTO>

        val reminderData = retriveReminder.data

        assertThat(reminderData, notNullValue())
        assertThat(reminderData.id, `is`(newReminder.id))
        assertThat(reminderData.title, `is`(newReminder.title))
        assertThat(reminderData.description, `is`(newReminder.description))
        assertThat(reminderData.location, `is`(newReminder.location))
        assertThat(reminderData.latitude, `is`(newReminder.latitude))
        assertThat(reminderData.longitude, `is`(newReminder.longitude))

    }

    @Test
    fun deleteAllReminder() = runBlocking{

        val newReminder = ReminderDTO("Test Title","Test Description", "Test Location",0.0,0.0)

        localRepository.saveReminder(newReminder)

        localRepository.deleteAllReminders()

        val checkEmptyStatus = localRepository.getReminders() as Result.Success

        assertThat(checkEmptyStatus.data.isEmpty(), `is`(true))

    }

    @Test
    fun getReminder_Error_By_Id() = runBlocking {
        val newReminder = ReminderDTO("Test Title","Test Description", "Test Location",0.0,0.0)

        localRepository.saveReminder(newReminder)

        localRepository.deleteAllReminders()

        val checkEmptyStatus = localRepository.getReminder(newReminder.id) as Result.Error

        assertThat(checkEmptyStatus.message, Matchers.notNullValue())
    }

    @Test
    fun getAllReminders() = runBlocking {

        val reminder1 = ReminderDTO("Test Title","Test Description", "Test Location",0.0,0.0)
        val reminder2 = ReminderDTO("Test Title","Test Description", "Test Location",0.0,0.0)

        localRepository.saveReminder(reminder1)
        localRepository.saveReminder(reminder2)

        val reminders = localRepository.getReminders() as Result.Success
        val totalReminder = reminders.data

        assertThat(totalReminder.size, `is`(2))

    }

}