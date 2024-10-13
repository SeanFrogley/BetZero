package nz.ac.canterbury.seng303.betzero.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.betzero.datastore.Storage
import nz.ac.canterbury.seng303.betzero.models.DailyLogState
import nz.ac.canterbury.seng303.betzero.models.UserProfile
import java.time.LocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAdjusters

class HomeViewModel (
    private val userProfileStorage: Storage<UserProfile>,
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> get() = _userProfile

    val GOAL_INPUT_ERROR: String = "Please enter a goal that is not empty"
    val LIFE_EXPECTANCY = 85f //arbitary not too small number
    val DAYS_IN_A_WEEK = 7f
    val SECONDS_IN_A_DAY = 86400f
    val TOTAL_DAYS_IN_YEAR = 365f


    init {
        viewModelScope.launch {
            try {
                val userProfiles = userProfileStorage.getAll().first()
                if (userProfiles.isNotEmpty()) {
                    _userProfile.value = userProfiles.first()
                } else {
                    _userProfile.value = null
                }
            } catch (e: Exception) {
                _userProfile.value = null
            }
        }
    }

    fun toggleHasLogged(dailyLogState: DailyLogState) {
        viewModelScope.launch {
            //update userProfile lastLogged
        }
    }

    /**
     * Returns the percentage of the user's life based on their age.
     */
    private fun calculateLife(age: Int) : Float {
        if (age < 0) return 0f

        return if (age >= LIFE_EXPECTANCY) {
            100f
        } else {
            (age / LIFE_EXPECTANCY.toFloat())
        }
    }

    /**
     * Helper function to calculate the percentages through the current month, week, and day.
     */
    private fun calculatePercentThroughTime(): List<Float> {
        val now = LocalDateTime.now()

        val percentThroughMonth = (now.dayOfMonth.toFloat() / now.with(TemporalAdjusters.lastDayOfMonth()).dayOfMonth.toFloat())
        val percentThroughWeek = (now.get(ChronoField.DAY_OF_WEEK).toFloat() / DAYS_IN_A_WEEK)
        val percentThroughDay = (now.toLocalTime().toSecondOfDay().toFloat() / SECONDS_IN_A_DAY)
        val percentThroughYear = (now.dayOfYear.toFloat() / TOTAL_DAYS_IN_YEAR)

        return listOf( percentThroughYear, percentThroughMonth, percentThroughWeek, percentThroughDay)
    }

    /**
     * Main function to return the percentages for life, month, week, day, and year based on the user's age.
     */
    fun calculateTimePercentages(age: Int): List<Float> {
        val percentThroughLife = calculateLife(age)
        val timePercentages = calculatePercentThroughTime()

        return listOf(percentThroughLife) + timePercentages
    }

    /**
     * uses the user id to store the edited user
     */
    fun editGoals(userProfile: UserProfile, goals: List<String>?) {
        viewModelScope.launch {
            val editUserProfile = UserProfile(
                userProfile.id,
                userProfile.name,
                userProfile.age,
                userProfile.totalSpent,
                userProfile.totalSaved,
                userProfile.dailySavings,
                userProfile.gamblingStartDate,
                userProfile.lastGambledDate,
                goals,
                userProfile.isDarkMode,
                userProfile.isUserEnforcedTheme
            )

            try {
                val result = userProfileStorage.edit(userProfile.getIdentifier(), editUserProfile).first()
                if (result == 1) {
                    Log.d("USER_PROFILE_VM", "User profile goals edited successfully")
                    _userProfile.value = userProfile
                } else {
                    Log.e("USER_PROFILE_VM", "User profile goal update failed")
                }
                userProfileStorage.getAll()
                    .collect { profiles ->
                        Log.i("UpdateHomeViewModel", "User Profiles: $profiles")
                    }
            } catch (exception: Exception) {
                Log.e("USER_PROFILE_VM", "Could not update user profile: $exception")
            }
        }
    }
}
