package nz.ac.canterbury.seng303.betzero.models

import UserUtil.calculateDaysBetween
import UserUtil.roundToTwoDecimalPlaces
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * user profile stores all of the meta data for a user. these are sample attributes
 */
class UserProfile (
    val id: Int,
    val name: String,
    val totalSpent: Double,
    val totalSaved: Double,
    val dailySavings: Double,
    val gamblingStartDate: Date,
    val startDate: Date
) : Identifiable {
    override fun getIdentifier(): Int {
        return id;
    }

    override fun toString(): String {
        return "UserProfile(id=$id, name=$name, totalSpent=$totalSpent, totalSaved=$totalSaved, dailySavings=$dailySavings, gamblingStartDate=$gamblingStartDate, startDate=$startDate)"
    }
}