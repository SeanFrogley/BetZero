package nz.ac.canterbury.seng303.betzero.models

import java.util.Date

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
    val lastGambledDate: Date
) : Identifiable {
    override fun getIdentifier(): Int {
        return id;
    }

    override fun toString(): String {
        return "UserProfile(id=$id, name=$name, totalSpent=$totalSpent, totalSaved=$totalSaved, dailySavings=$dailySavings, gamblingStartDate=$gamblingStartDate, startDate=$lastGambledDate)"
    }
}