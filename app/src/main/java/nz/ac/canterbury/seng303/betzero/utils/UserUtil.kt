import nz.ac.canterbury.seng303.betzero.models.UserProfile
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

object UserUtil {

    fun calculateDaysBetween(startDate: Date, endDate: Date): Long {
        val diffInMillis = endDate.time - startDate.time
        return TimeUnit.MILLISECONDS.toDays(diffInMillis)
    }

    fun roundToTwoDecimalPlaces(value: Double): Double {
        return BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }

    fun calculateDailySavings(totalSpent: Double, gamblingStartDate: Date, serviceStartDate: Date): Double {
        val daysBetween = calculateDaysBetween(gamblingStartDate, serviceStartDate)

        return if (daysBetween > 0) {
            roundToTwoDecimalPlaces(totalSpent / daysBetween)
        } else {
            0.0
        }
    }

    fun createUser(): UserProfile {
        val calendar = Calendar.getInstance()

        // set gambling start date to 1 year ago
        calendar.add(Calendar.YEAR, -1)
        val gamblingStartDate = calendar.time

        // set start date to 1 month ago
        calendar.time = Date()
        calendar.add(Calendar.MONTH, -1)
        val startDate = calendar.time

        val id = 1
        val name = "John Doe"
        val totalSpent = 1000.0
        val totalSaved = 0.0

        val dailySavings = UserUtil.calculateDailySavings(
            totalSpent = totalSpent,
            gamblingStartDate = gamblingStartDate,
            serviceStartDate = startDate
        )

        val roundedTotalSpent = roundToTwoDecimalPlaces(totalSpent)
        val roundedTotalSaved = roundToTwoDecimalPlaces(totalSaved)

        return UserProfile(
            id = id,
            name = name,
            totalSpent = roundedTotalSpent,
            totalSaved = roundedTotalSaved,
            dailySavings = dailySavings,
            gamblingStartDate = gamblingStartDate,
            startDate = startDate
        )
    }
}