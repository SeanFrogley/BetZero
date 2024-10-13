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

class HomeViewModel (
    private val userProfileStorage: Storage<UserProfile>,
    private val dailyLogStateStorage: Storage<DailyLogState>

) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> get() = _userProfile

    private val _dailyLogState = MutableStateFlow<DailyLogState?>(null)
    val dailyLogState : StateFlow<DailyLogState?> get() = _dailyLogState

    val GOAL_INPUT_ERROR: String = "Please enter a goal that is not empty"
    val LIFE_EXPECTANCY: Int = 85 //arbitary not too small number

    init {
        viewModelScope.launch {
            try {
                val userProfiles = userProfileStorage.getAll().first()
                val hasLogged = dailyLogStateStorage.getAll().first()
                if (userProfiles.isNotEmpty()) {
                    _userProfile.value = userProfiles.first()
                } else {
                    _userProfile.value = null
                }

                if (hasLogged.isNotEmpty()) {
                    _dailyLogState.value = hasLogged.first()
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
            val updatedDailyLogState = DailyLogState(
                dailyLogState.id,
                dailyLogState.date, //new date?
                !dailyLogState.loggedToday, //flip the daily log
            )

            try {
                val result =
                    dailyLogStateStorage.edit(dailyLogState.getIdentifier(), updatedDailyLogState).first()
                if (result == 1) {
                    Log.d("DAILY_LOG_VM", "Daily Log State edited successfully")
                    _dailyLogState.value = dailyLogState
                } else {
                    Log.e("DAILY_LOG_VM", "Daily Log State update failed")
                }
                userProfileStorage.getAll()
                    .collect { dailyLogState ->
                        Log.i("UpdateHomeViewModel", "Daily Log State: $dailyLogState")
                    }
            } catch (exception: Exception) {
                Log.e("USER_PROFILE_VM", "Could not update daily log state: $exception")
            }
        }
    }

    /**
     * Returns the percentage of the user's life based on their age.
     */
    fun calculateLife(age: Int) : Float {
        if (age < 0) return 0f

        return if (age >= LIFE_EXPECTANCY) {
            100f
        } else {
            (age / LIFE_EXPECTANCY.toFloat())
        }
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
