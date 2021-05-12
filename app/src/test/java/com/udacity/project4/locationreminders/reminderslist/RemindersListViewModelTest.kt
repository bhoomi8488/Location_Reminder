package com.udacity.project4.locationreminders.reminderslist

import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
class RemindersListViewModelTest {

    private lateinit var dataSource: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel
    private lateinit var context: Context

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupRemindersListViewModel() {
        context = ApplicationProvider.getApplicationContext()
        dataSource = FakeDataSource()
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun loadReminders_showloading_value() = mainCoroutineRule.runBlockingTest {
        dataSource.saveReminder(ReminderDTO("Title Test", "Description Test", "Location Test", 0.0, 0.0))

        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadReminders_Show_Error_SnackBar() = mainCoroutineRule.runBlockingTest {

        dataSource.setReturnValue(true)

        viewModel.loadReminders()

        assertThat(viewModel.showSnackBar.getOrAwaitValue(), notNullValue())

    }

    @Test
    fun loadReminders_Show_Empty_List()= mainCoroutineRule.runBlockingTest {
        dataSource.deleteAllReminders()

        viewModel.loadReminders()

        assertThat(viewModel.showNoData.getOrAwaitValue (), `is`(true))
    }




}