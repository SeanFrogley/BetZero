package nz.ac.canterbury.seng303.betzero.viewmodels

import UserUtil.calculateDailySavings
import UserUtil.calculateDaysBetween
import UserUtil.roundToTwoDecimalPlaces
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nz.ac.canterbury.seng303.betzero.datastore.Storage
import nz.ac.canterbury.seng303.betzero.models.UserProfile
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class GettingStartedViewModel(
    private val userProfileStorage: Storage<UserProfile>
) : ViewModel() {

    var _userProfile = mutableStateOf<UserProfile?>(null)

    fun saveUserProfile(
        name: String,
        totalSpent: Double,
        totalSaved: Double,
        gamblingStartDate: Date
    ) = viewModelScope.launch {
        val startDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        val dailySavings = calculateDailySavings(
            totalSpent = totalSpent,
            gamblingStartDate = gamblingStartDate,
            serviceStartDate = startDate
        )
        val roundedTotalSpent = roundToTwoDecimalPlaces(totalSpent)
        val roundedTotalSaved = roundToTwoDecimalPlaces(totalSaved)

        val userProfile = UserProfile(
            id = Random.nextInt(0, Int.MAX_VALUE),
            name = name,
            totalSpent = roundedTotalSpent,
            totalSaved = roundedTotalSaved,
            gamblingStartDate = gamblingStartDate,
            startDate = startDate,
            dailySavings = dailySavings
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
