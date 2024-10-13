package nz.ac.canterbury.seng303.betzero.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object CalendarUtil {
    fun getMonthName(month: Int): String {
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val cal = Calendar.getInstance().apply {
            set(Calendar.MONTH, month)
        }
        return monthFormat.format(cal.time)
    }

    fun stripTime(date: Date): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.time
    }
}