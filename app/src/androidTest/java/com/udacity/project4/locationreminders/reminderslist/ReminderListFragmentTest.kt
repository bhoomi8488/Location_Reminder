package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    private lateinit var datasource: ReminderDataSource
    private lateinit var appContext: Application

    val reminder = ReminderDTO(
        "Title Test",
        "Description Test",
        "Location Test",
        0.0,
        0.0
    )

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        stopKoin()
        appContext = getApplicationContext()
        datasource = FakeDataSource()
        val myModules = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    datasource as ReminderDataSource
                )
            }
        }

        startKoin {
            modules(listOf(myModules))
        }

        runBlocking {
            datasource.deleteAllReminders()
            datasource.saveReminder(reminder)
        }
    }

    @Test
    fun display_Error_on_emplty_reinder_list() {

        runBlocking {
            datasource.deleteAllReminders()
        }
        val scenario =
            launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)

        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        onView(withId(R.id.noDataTextView)).check(ViewAssertions.matches(isDisplayed()))
        onView(withText("No Data")).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun navigate_to_new_reminder_screen_on_fab_click() {
        val scenario =
            launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }

    @Test
    fun display_save_reminder_on_screen() {
        val scenario =
            launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withText(reminder.title)).check(ViewAssertions.matches(isDisplayed()))
        onView(withText(reminder.description)).check(ViewAssertions.matches(isDisplayed()))
        onView(withText(reminder.location)).check(ViewAssertions.matches(isDisplayed()))
    }
}