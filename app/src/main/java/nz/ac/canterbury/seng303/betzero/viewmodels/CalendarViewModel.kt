package nz.ac.canterbury.seng303.betzero.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nz.ac.canterbury.seng303.betzero.datastore.Storage
import nz.ac.canterbury.seng303.betzero.models.DailyLog
import nz.ac.canterbury.seng303.betzero.models.RelapseLog
import nz.ac.canterbury.seng303.betzero.models.UserProfile
import nz.ac.canterbury.seng303.betzero.utils.UserUtil.calculateDailySavings
import nz.ac.canterbury.seng303.betzero.utils.UserUtil.calculateTotalSavings
import nz.ac.canterbury.seng303.betzero.utils.UserUtil.roundToTwoDecimalPlaces
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class CalendarViewModel(
    private val userProfileStorage: Storage<UserProfile>,
    private val dailyLogStorage: Storage<DailyLog>,
    private val relapseLogStorage: Storage<RelapseLog>
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> get() = _userProfile

    private val _dailyLogs = MutableStateFlow<List<DailyLog>>(emptyList())
    val dailyLogs: StateFlow<List<DailyLog>> get() = _dailyLogs

    private val _relapseLogs = MutableStateFlow<List<RelapseLog>>(emptyList())
    val relapseLogs: StateFlow<List<RelapseLog>> get() = _relapseLogs

    init {
        viewModelScope.launch {
            try {
                // Retrieve user profile
                val userProfiles = userProfileStorage.getAll().first()
                if (userProfiles.isNotEmpty()) {
                    _userProfile.value = userProfiles.first()
                } else {
                    _userProfile.value = null
                }
            } catch (e: Exception) {
                _userProfile.value = null
            }

            try {
                val logs = dailyLogStorage.getAll().first()
                _dailyLogs.value = logs
            } catch (e: Exception) {
                _dailyLogs.value = emptyList()
            }

            try {
                val relapseLogEntries = relapseLogStorage.getAll().first()
                _relapseLogs.value = relapseLogEntries
            } catch (e: Exception) {
                _relapseLogs.value = emptyList()
            }
        }
    }

    fun updateUserProfile(
        id: Int,
        name: String,
        age: Int,
        totalSpent: Double,
        gamblingStartDate: Date,
        lastGambledDate: Date
    ) = viewModelScope.launch(Dispatchers.IO) {
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

        val userProfile = UserProfile(
            id = id,
            name = name,
            age = age,
            totalSpent = roundedTotalSpent,
            totalSaved = roundedTotalSaved,
            gamblingStartDate = gamblingStartDate,
            dailySavings = dailySavings,
            lastGambledDate = lastGambledDate
        )

        Log.d("DataStoreInsert", "Updating user profile: $userProfile")

        try {
            // Perform DataStore update in IO thread
            userProfileStorage.edit(userProfile.getIdentifier(), userProfile)
                .flowOn(Dispatchers.IO) // Ensure flow operates on IO
                .collect { result ->
                    if (result == 1) {
                        withContext(Dispatchers.Main) { // Switch to Main thread for UI updates
                            Log.d("USER_PROFILE_VM", "User profile updated successfully")
                            _userProfile.value = userProfile
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Log.e("USER_PROFILE_VM", "User profile update failed")
                        }
                    }
                }
        } catch (exception: Exception) {
            withContext(Dispatchers.Main) {
                Log.e("USER_PROFILE_VM", "Could not update user profile: $exception")
            }
        }
    }

    fun insertRelapseLog(selectedDate: String, amountSpent: String) = viewModelScope.launch {
        val parsedAmount = amountSpent.toDoubleOrNull() ?: 0.0
        val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate) ?: Date()

        val newRelapseLog = RelapseLog(
            id = Random.nextInt(0, Int.MAX_VALUE),
            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(parsedDate),
            amountSpent = parsedAmount
        )

        // Insert the new relapse log into the storage
        relapseLogStorage.insert(newRelapseLog).collect { result ->
            if (result == 1) {
                // Update the relapse logs state
                _relapseLogs.value = _relapseLogs.value + newRelapseLog
                Log.d("RelapseLog", "Relapse log inserted successfully")
            } else {
                Log.e("RelapseLog", "Failed to insert relapse log")
            }
        }
    }
}
