package nz.ac.canterbury.seng303.betzero.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GettingStartedViewModel : ViewModel() {
    var userName = mutableStateOf("")
    var totalSpent = mutableStateOf("")
    var selectedDate = mutableStateOf("")

    fun saveUserInfo() {
        viewModelScope.launch {
            println("User Info Saved - Name: $userName, Total Spent: $totalSpent, Start Date: $selectedDate")
        }
    }
}
