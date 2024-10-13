package nz.ac.canterbury.seng303.betzero.utils

import java.time.LocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAdjusters

object HomeStatsCalculatorUtil {
    private const val DAYS_IN_A_WEEK = 7f
    private const val SECONDS_IN_A_DAY = 86400f
    private const val TOTAL_DAYS_IN_YEAR = 365f
    private const val LIFE_EXPECTANCY = 85f // Arbitrary life expectancy

    /**
     * Calculates the percentages through the current year, month, week, and day.
     * Returns a list of pairs, where each pair contains the percentage and the corresponding time period description.
     */
    fun calculatePercentThroughTime(): List<Pair<Float, String>> {
        val now = LocalDateTime.now()

        val percentThroughMonth = (now.dayOfMonth.toFloat() / now.with(TemporalAdjusters.lastDayOfMonth()).dayOfMonth.toFloat())
        val percentThroughWeek = (now.get(ChronoField.DAY_OF_WEEK).toFloat() / DAYS_IN_A_WEEK)
        val percentThroughDay = (now.toLocalTime().toSecondOfDay().toFloat() / SECONDS_IN_A_DAY)
        val percentThroughYear = (now.dayOfYear.toFloat() / TOTAL_DAYS_IN_YEAR)

        return listOf(
            percentThroughYear to "Year",
            percentThroughMonth to "Month",
            percentThroughWeek to "Week",
            percentThroughDay to "Day"
        )
    }

    /**
     * Returns the percentage of the user's life based on their age.
     * Returns a pair containing the percentage and a description of the time period ("Life").
     */
    fun calculateLife(age: Int): Pair<Float, String> {
        if (age < 0) return 0f to "Life"

        return if (age >= LIFE_EXPECTANCY) {
            100f to "Life"
        } else {
            (age / LIFE_EXPECTANCY) to "Life"
        }
    }
}