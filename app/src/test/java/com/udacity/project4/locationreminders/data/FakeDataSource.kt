package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    var returnError = false
    var reminders = mutableListOf<ReminderDTO>()

    fun setReturnValue(status: Boolean) {
        returnError = status
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (returnError) {
            return Result.Error("Test : Get Reminder Error")
        } else {
            return Result.Success(reminders)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (returnError) {
            return Result.Error("Test : Get Reminder with ID Error")
        } else {
            val reminder = reminders.find { it.id == id }
            reminder?.let {
                return Result.Success(reminder)
            }
        }
        return Result.Error("Test : $id not found for reminder")
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }


}