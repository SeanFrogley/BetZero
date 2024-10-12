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

//    /* TODO This is for testing purposes and should be deleted when analytics screen is completed but might be useful for other features as well */
//    init {
//        viewModelScope.launch {
//            try {
//                val userProfiles = userProfileStorage.getAll().first()
//                if (userProfiles.isEmpty()) {
//                    createUserProfile()
//                    _userProfileExists.value = false
//                } else {
//                    _userProfileExists.value = true
//                }
//            } catch (e: Exception) {
//                _userProfileExists.value = false
//            }
//        }
//    }
//
//    private fun createUserProfile() {
//        viewModelScope.launch {
//            try {
//                Log.d("UserProfileCreation", "Starting to create a new user profile...")
//                val newUser = UserUtil.createUser()
//                Log.d("UserProfileCreation", "New user profile created: $newUser")
//                val result = userProfileStorage.insert(newUser).first()
//                if (result == 1) {
//                    Log.d("UserProfileCreation", "User profile inserted successfully")
//                    _userProfileExists.value = true
//                    userProfileStorage.getAll().collect { profiles ->
//                        Log.i("UserProfileCreation", "All User Profiles: $profiles")
//                    }
//                } else {
//                    Log.e("UserProfileCreation", "User profile insertion failed")
//                    _userProfileExists.value = false
//                }
//
//            } catch (exception: Exception) {
//                Log.e("UserProfileCreationError", "Error while creating or saving user profile: ${exception.message}", exception)
//                _userProfileExists.value = false
//            }
//        }
//    }
}