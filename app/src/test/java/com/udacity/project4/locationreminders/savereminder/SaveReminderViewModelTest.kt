package com.udacity.project4.locationreminders.savereminder

import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SaveReminderViewModelTest {

    private lateinit var dataSource: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel
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
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun saveReminder_Check_Status() = mainCoroutineRule.runBlockingTest {

        mainCoroutineRule.pauseDispatcher()

        viewModel.saveReminder(ReminderDataItem("Test Reminder","Test Discription", "GooglrPlex",37.42224449209498,
            -122.08403605065007))

            assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(viewModel.showLoading.getOrAwaitValue(), `is` (false))
        assertEquals(viewModel.showToast.getOrAwaitValue(), context.getString(R.string.reminder_saved))
        assertEquals(viewModel.navigationCommand.getOrAwaitValue(), NavigationCommand.Back)

    }

    @Test
    fun onClear_Check_Null_Status() {
        viewModel.onClear()

        assertThat(viewModel.reminderTitle.getOrAwaitValue(), nullValue())
        assertThat(viewModel.reminderDescription.getOrAwaitValue(), nullValue())
        assertThat(viewModel.reminderSelectedLocationStr.getOrAwaitValue(), nullValue())
        assertThat(viewModel.selectedPOI.getOrAwaitValue(), nullValue())
        assertThat(viewModel.latitude.getOrAwaitValue(), nullValue())
        assertThat(viewModel.longitude.getOrAwaitValue(), nullValue())
    }

    @Test
    fun validateEnteredData_Check_Null_or_Not(){

        dataSource.setReturnValue(true)

        viewModel.validateAndSaveReminder(ReminderDataItem(null,"Test Description", "Test Location", 56.326,48.52))
        assertEquals(viewModel.showSnackBarInt.getOrAwaitValue (), R.string.err_enter_title)

        viewModel.validateAndSaveReminder(ReminderDataItem("Test Title","Test Description", null, 56.326,48.52))
        assertEquals(viewModel.showSnackBarInt.getOrAwaitValue (), R.string.err_select_location)
    }

}