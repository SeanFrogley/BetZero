package nz.ac.canterbury.seng303.betzero.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object InputValidation {
    private var namePattern = "^[a-zA-Z\\s]+$".toRegex()


    /**
     * Validates the user name input.
     * @param userName The input user name string.
     * @return True if the user name is not empty, otherwise false.
     */
    fun validateUsersName(userName: String): Boolean {
        return userName.isNotEmpty() && namePattern.matches(userName)
    }

    /**
     * Validates the total spent input.
     * @param totalSpent The input total spent string.
     * @return True if the total spent is not empty and is a valid number, otherwise false.
     */
    fun validateTotalSpent(totalSpent: String): Boolean {
        return totalSpent.isNotEmpty() && totalSpent.toDoubleOrNull()?.let { it >= 0 } == true
    }

    /**
     * Validates the selected date input.
     * @param selectedDate The input selected date string.
     * @return True if the selected date is not empty, otherwise false.
     */
    fun validateDate(selectedDate: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val parsedDate = LocalDate.parse(selectedDate, formatter)
            selectedDate.isNotEmpty() && !parsedDate.isAfter(LocalDate.now())
        } catch (e: DateTimeParseException) {
            false
        }
    }
}
