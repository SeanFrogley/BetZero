package nz.ac.canterbury.seng303.betzero.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import nz.ac.canterbury.seng303.betzero.datastore.Storage
import nz.ac.canterbury.seng303.betzero.models.DailyLog

class DailyLogViewModel (
    private val dailyLogStorage: Storage<DailyLog>
) : ViewModel() {

    private val _logs = MutableStateFlow<List<DailyLog>>(emptyList())
    val logs: StateFlow<List<DailyLog>> get() = _logs

    //functions to handle daily log
}