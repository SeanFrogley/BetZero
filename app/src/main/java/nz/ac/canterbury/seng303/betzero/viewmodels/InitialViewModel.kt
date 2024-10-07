package nz.ac.canterbury.seng303.betzero.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.betzero.datastore.Storage
import nz.ac.canterbury.seng303.betzero.models.UserProfile

class InitialViewModel(
    private val userProfileStorage: Storage<UserProfile>
) : ViewModel() {

    private val _userProfileExists = MutableStateFlow<Boolean?>(null)
    val userProfileExists: StateFlow<Boolean?> get() = _userProfileExists

    init {
        viewModelScope.launch {
            try {
                val userProfiles = userProfileStorage.getAll().first()
                _userProfileExists.value = userProfiles.isNotEmpty()
            } catch (e: Exception) {
                _userProfileExists.value = false
            }
        }
    }
}