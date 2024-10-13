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
import nz.ac.canterbury.seng303.betzero.utils.HomeStatsCalculatorUtil

class HomeViewModel (
    private val userProfileStorage: Storage<UserProfile>,
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> get() = _userProfile

    val GOAL_INPUT_ERROR: String = "Please enter a goal that is not empty"


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
     * Main function to return the percentages for life, month, week, day, and year based on the user's age.
     * Uses HomeStatsCalculatorUtil to calculate the percentages.
     */
    fun calculateTimePercentages(age: Int): List<Pair<Float, String>> {
        val percentThroughLife = HomeStatsCalculatorUtil.calculateLife(age)

        val timePercentages = HomeStatsCalculatorUtil.calculatePercentThroughTime()

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
