package nz.ac.canterbury.seng303.betzero.utils

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

    fun calculateProjectedSavings(dailySavings: Double): Pair<Double, Double> {
        val monthlySavings = roundToTwoDecimalPlaces(dailySavings * 30)
        val yearlySavings = roundToTwoDecimalPlaces(dailySavings * 365)
        return Pair(monthlySavings, yearlySavings)
    }

    fun calculateTotalSavings(dailySavings: Double, startDate: Date, endDate: Date): Double {
        val daysBetween = calculateDaysBetween(startDate, endDate)
        return roundToTwoDecimalPlaces(dailySavings * daysBetween)
    }

    fun createSavingsData(overallSavings: Double, dailySavings: Double, size: Int): List<Float> {
        return List(size) { index ->
            if (index == 0) overallSavings.toFloat() else (overallSavings + dailySavings * index).toFloat()
        }
    }

    fun createDateList(size: Int): List<Date> {
        return List(size) { index ->
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, index)
            calendar.time
        }
    }

    fun getAllDatesSinceStart(startDate: Date): List<Date> {
        val datesList = mutableListOf<Date>()
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        while (calendar.time.before(Date()) || calendar.time == Date()) {
            datesList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return datesList
    }

    fun createUser(): UserProfile {
        val calendar = Calendar.getInstance()

        // set gambling start date to 1 year ago
        calendar.add(Calendar.YEAR, -1)
        val gamblingStartDate = calendar.time

        // set last gambled date to 1 month ago
        calendar.time = Date()
        calendar.add(Calendar.MONTH, -1)
        val lastGambledDate = calendar.time

        val id = 1
        val name = "John Doe"
        val totalSpent = 1000.0
        val totalSaved = 0.0

        val dailySavings = calculateDailySavings(
            totalSpent = totalSpent,
            gamblingStartDate = gamblingStartDate,
            serviceStartDate = lastGambledDate
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
            lastGambledDate = lastGambledDate
        )
    }
}
