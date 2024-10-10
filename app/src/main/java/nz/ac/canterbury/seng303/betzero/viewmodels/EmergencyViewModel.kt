package nz.ac.canterbury.seng303.betzero.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.betzero.datastore.Storage
import nz.ac.canterbury.seng303.betzero.models.UserProfile

enum class SlotShape {
    Circle,
    Rectangle,
    Triangle
}

class EmergencyViewModel(
    private val userProfileStorage: Storage<UserProfile>
) : ViewModel() {
    var reel1 by mutableStateOf(SlotShape.Circle)
    var reel2 by mutableStateOf(SlotShape.Circle)
    var reel3 by mutableStateOf(SlotShape.Circle)

    var balance by mutableStateOf(100)
    var outcomeMessage by mutableStateOf("")

    var isSpinning by mutableStateOf(false)

    fun startSpinning() {
        isSpinning = true
        viewModelScope.launch {
            delay(3000)

            isSpinning = false

            reel1 = SlotShape.entries.toTypedArray().random()
            reel2 = SlotShape.entries.toTypedArray().random()
            reel3 = SlotShape.entries.toTypedArray().random()

            checkWin()
        }
    }

    private fun checkWin() {
        if (reel1 == reel2 && reel2 == reel3) {
            balance += 10
            outcomeMessage = "You win!"
        } else {
            balance -= 5
            outcomeMessage = "You lose!"
        }
    }
}
