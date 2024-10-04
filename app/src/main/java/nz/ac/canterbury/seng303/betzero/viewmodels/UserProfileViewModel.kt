package nz.ac.canterbury.seng303.betzero.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import nz.ac.canterbury.seng303.betzero.datastore.Storage
import nz.ac.canterbury.seng303.betzero.models.UserProfile


class UserProfileViewModel (
    private val userProfileStorage: Storage<UserProfile>
) : ViewModel() {

    private val _userProfile = MutableStateFlow<List<UserProfile>>(emptyList())
    val userProfile: StateFlow<List<UserProfile>> get() = _userProfile

    //functions to handle UserProfile

}