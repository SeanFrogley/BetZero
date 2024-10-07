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
    val startDate: Date
) : Identifiable {
    override fun getIdentifier(): Int {
        return id;
    }
}