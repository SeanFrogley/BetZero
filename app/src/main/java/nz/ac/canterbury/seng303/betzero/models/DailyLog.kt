package nz.ac.canterbury.seng303.betzero.models

import java.util.Date

/**
 * stores the 'daily entry' data for data submitted by the user when the log in (pop up summary screen) these are sample attributes
 */
class DailyLog (
    val id: Int,
    val feeling: String, //emoji
    val voiceMemo: String, //filepath from file system?
    val date: String //date of the entry
) : Identifiable {

    override fun getIdentifier(): Int {
        return id;
    }

    override fun toString(): String {
        return "DailyLog(id=$id, date=$date)"
    }
}