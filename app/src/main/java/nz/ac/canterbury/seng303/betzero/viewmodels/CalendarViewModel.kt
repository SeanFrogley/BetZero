package nz.ac.canterbury.seng303.betzero.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.betzero.datastore.Storage
import nz.ac.canterbury.seng303.betzero.models.DailyLog
import nz.ac.canterbury.seng303.betzero.models.UserProfile
import java.util.Date

class CalendarViewModel(
    private val userProfileStorage: Storage<UserProfile>,
    private val dailyLogStorage: Storage<DailyLog>
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> get() = _userProfile

    private val _dailyLogs = MutableStateFlow<List<DailyLog>>(emptyList())
    val dailyLogs: StateFlow<List<DailyLog>> get() = _dailyLogs

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
            try {
                val logs = dailyLogStorage.getAll().first()
                _dailyLogs.value = logs
            } catch (e: Exception) {
                _dailyLogs.value = emptyList()
            }
        }
    }

    fun updateUserProfile(amountSpent: Double, relapseDate: Date) {
        viewModelScope.launch {
            _userProfile.value?.let { currentUserProfile ->
                val updatedTotalSpent = currentUserProfile.totalSpent + amountSpent

                val updatedLastGambledDate =
                    if (relapseDate.after(currentUserProfile.lastGambledDate)) {
                        relapseDate
                    } else {
                        currentUserProfile.lastGambledDate
                    }

                val updatedUserProfile = UserProfile(
                    id = currentUserProfile.id,
                    name = currentUserProfile.name,
                    totalSpent = updatedTotalSpent,
                    totalSaved = currentUserProfile.totalSaved,
                    dailySavings = currentUserProfile.dailySavings,
                    gamblingStartDate = currentUserProfile.gamblingStartDate,
                    lastGambledDate = updatedLastGambledDate
                )

                userProfileStorage.edit(currentUserProfile.id, updatedUserProfile)
                    .collect { result ->
                        if (result == 1) {
                            _userProfile.value = updatedUserProfile
                        } else {
                            // error
                        }
                    }
            }
        }
    }
}