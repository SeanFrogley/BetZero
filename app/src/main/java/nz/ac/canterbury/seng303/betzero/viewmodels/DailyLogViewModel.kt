package nz.ac.canterbury.seng303.betzero.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.betzero.datastore.Storage
import nz.ac.canterbury.seng303.betzero.models.DailyLog

class DailyLogViewModel (
    private val dailyLogStorage: Storage<DailyLog>
) : ViewModel() {

    private val _logs = MutableStateFlow<List<DailyLog>>(emptyList())
    val logs: StateFlow<List<DailyLog>> get() = _logs

    init {
        fetchDailyLogs()
    }

    private fun fetchDailyLogs() {
        viewModelScope.launch {
            dailyLogStorage.getAll().collect { logs ->
                _logs.value = logs
            }
        }
    }

    fun saveDailyLog(dailyLog: DailyLog) {
        viewModelScope.launch {
            dailyLogStorage.insert(dailyLog).collect {
                fetchDailyLogs() // Refresh the logs after saving
            }
        }
    }
}