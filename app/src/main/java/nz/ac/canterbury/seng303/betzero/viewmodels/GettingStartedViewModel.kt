package nz.ac.canterbury.seng303.betzero.viewmodels

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
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.random.Random

class GettingStartedViewModel(
    private val userProfileStorage: Storage<UserProfile>
) : ViewModel() {

    var _userProfile = mutableStateOf<UserProfile?>(null)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun saveUserProfile(
        name: String,
        totalSpent: Double,
        totalSaved: Double,
        gamblingStartDate: Date
    ) = viewModelScope.launch {
        val userProfile = UserProfile(
            id = Random.nextInt(0, Int.MAX_VALUE),
            name = name,
            totalSpent = totalSpent,
            totalSaved = totalSaved,
            gamblingStartDate = gamblingStartDate,
            startDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
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

            // Fetch and log all user profiles
            userProfileStorage.getAll()
                .collect { profiles ->
                    Log.i("GettingStartedViewModel", "User Profiles: $profiles")
                }
        } catch (exception: Exception) {
            Log.e("USER_PROFILE_VM", "Could not insert user profile: $exception")
        }
    }
}
