package nz.ac.canterbury.seng303.betzero.viewmodels

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.betzero.datastore.Storage
import nz.ac.canterbury.seng303.betzero.models.UserProfile

class PreferencesViewModel (
    private val userProfileStorage: Storage<UserProfile>,
) : ViewModel() {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> get() = _userProfile

    private var isSystemInDarkTheme: Boolean = false

    private val _isDarkTheme = MutableStateFlow(isSystemInDarkTheme) // Initially follow system theme
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    private fun updateTheme() {
        viewModelScope.launch {
            _userProfile.value?.let { profile ->
                if (profile.isUserEnforcedTheme) {
                    _isDarkTheme.emit(profile.isDarkMode)
                } else {
                    _isDarkTheme.emit(isSystemInDarkTheme)
                }
            }
        }
    }

    // This function can be called when user changes theme preference
    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            _isDarkTheme.emit(enabled)
        }
    }

    fun setIsSystemInDarkTheme(enabled : Boolean) {
        isSystemInDarkTheme = enabled
        updateTheme()
    }


    init {
        viewModelScope.launch {
            try {
                val userProfiles = userProfileStorage.getAll().first()
                if (userProfiles.isNotEmpty()) {
                    _userProfile.value = userProfiles.first()
                    updateTheme()
                } else {
                    _userProfile.value = null
                }
            } catch (e: Exception) {
                _userProfile.value = null
            }
        }
    }



}