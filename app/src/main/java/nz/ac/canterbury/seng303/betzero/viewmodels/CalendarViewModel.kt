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

class CalendarViewModel(
    private val userProfileStorage: Storage<UserProfile>,
    private val dailyLogStorage: Storage<DailyLog> // Injecting dailyLogStorage
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> get() = _userProfile

    private val _dailyLogs = MutableStateFlow<List<DailyLog>>(emptyList())
    val dailyLogs: StateFlow<List<DailyLog>> get() = _dailyLogs

    init {
        // Fetching UserProfile
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

        // Fetching DailyLogs
        viewModelScope.launch {
            try {
                val logs = dailyLogStorage.getAll().first()
                _dailyLogs.value = logs
            } catch (e: Exception) {
                _dailyLogs.value = emptyList()
            }
        }
    }
}
