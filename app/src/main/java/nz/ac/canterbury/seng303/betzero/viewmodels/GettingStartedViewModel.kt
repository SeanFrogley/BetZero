package nz.ac.canterbury.seng303.betzero.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.betzero.datastore.Storage
import nz.ac.canterbury.seng303.betzero.models.DailyLogState
import nz.ac.canterbury.seng303.betzero.models.UserProfile
import nz.ac.canterbury.seng303.betzero.utils.UserUtil.calculateDailySavings
import nz.ac.canterbury.seng303.betzero.utils.UserUtil.calculateTotalSavings
import nz.ac.canterbury.seng303.betzero.utils.UserUtil.roundToTwoDecimalPlaces
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
import kotlin.random.Random

class GettingStartedViewModel(
    private val userProfileStorage: Storage<UserProfile>
) : ViewModel() {

    var _userProfile = mutableStateOf<UserProfile?>(null)

    fun saveUserProfile(
        name: String,
        age: Int,
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
        val balance = 10000
        val roundedTotalSpent = roundToTwoDecimalPlaces(totalSpent)
        val roundedTotalSaved = roundToTwoDecimalPlaces(totalSaved)

        val userProfile = UserProfile(
            id = Random.nextInt(0, Int.MAX_VALUE),
            name = name,
            age = age,
            totalSpent = roundedTotalSpent,
            totalSaved = roundedTotalSaved,
            gamblingStartDate = gamblingStartDate,
            dailySavings = dailySavings,
            lastGambledDate = lastGambledDate,
            notificationTime = LocalTime.NOON
        )
        Log.d("DataStoreInsert", "Inserting user profile: $userProfile")
        try {
            val result = userProfileStorage.insert(userProfile).first()
            if (result == 1) {
                Log.d("USER_PROFILE_VM", "User profile inserted successfully")
                _userProfile.value = userProfile
            } else {
                Log.e("USER_PROFILE_VM", "User profile insertion failed")
            }
            userProfileStorage.getAll()
                .collect { profiles ->
                    Log.i("GettingStartedViewModel", "User Profiles: $profiles")
                }
        } catch (exception: Exception) {
            Log.e("USER_PROFILE_VM", "Could not insert user profile: $exception")
        }
    }

}
