package nz.ac.canterbury.seng303.betzero.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.betzero.datastore.Storage
import nz.ac.canterbury.seng303.betzero.models.UserProfile
import nz.ac.canterbury.seng303.betzero.utils.UserUtil.calculateDailySavings
import nz.ac.canterbury.seng303.betzero.utils.UserUtil.calculateTotalSavings
import nz.ac.canterbury.seng303.betzero.utils.UserUtil.roundToTwoDecimalPlaces
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class UpdateUserProfileViewModel (
    private val userProfileStorage: Storage<UserProfile>
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> get() = _userProfile

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


    fun updateUserProfile(
        id: Int,
        name: String,
        totalSpent: Double,
        gamblingStartDate: Date,
        lastGambledDate: Date
    ) = viewModelScope.launch {
        val startDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        val dailySavings = calculateDailySavings(
            totalSpent = totalSpent,
            gamblingStartDate = gamblingStartDate,
            serviceStartDate = startDate
        )
        val totalSaved = calculateTotalSavings(
            dailySavings = dailySavings,
            startDate = gamblingStartDate,
            endDate = lastGambledDate
        )

        val roundedTotalSpent = roundToTwoDecimalPlaces(totalSpent)
        val roundedTotalSaved = roundToTwoDecimalPlaces(totalSaved)

        val currentProfile = _userProfile.value

        val userProfile = UserProfile(
            id = id,
            name = name,
            totalSpent = roundedTotalSpent,
            totalSaved = roundedTotalSaved,
            gamblingStartDate = gamblingStartDate,
            dailySavings = dailySavings,
            lastGambledDate = lastGambledDate,

            isDarkMode = currentProfile?.isDarkMode ?: false,
            isUserEnforcedTheme = currentProfile?.isUserEnforcedTheme ?: false
        )
        Log.d("DataStoreInsert", "Updating user profile: $userProfile")
        try {
            val result = userProfileStorage.edit(userProfile.getIdentifier(), userProfile).first()
            if (result == 1) {
                Log.d("USER_PROFILE_VM", "User profile updated successfully")
                _userProfile.value = userProfile
            } else {
                Log.e("USER_PROFILE_VM", "User profile update failed")
            }
            userProfileStorage.getAll()
                .collect { profiles ->
                    Log.i("UpdateUserProfileViewModel", "User Profiles: $profiles")
                }
        } catch (exception: Exception) {
            Log.e("USER_PROFILE_VM", "Could not update user profile: $exception")
        }

    }
}