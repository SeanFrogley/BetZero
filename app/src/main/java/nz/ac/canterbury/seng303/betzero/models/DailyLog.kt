package nz.ac.canterbury.seng303.betzero.models

/**
 * stores the 'daily entry' data for data submitted by the user when the log in (pop up summary screen) these are sample attributes
 */
class DailyLog (
    val id: Int,
    val feeling: String, //emoji
    val voiceMemo: String, //filepath from file system?
) : Identifiable {

    override fun getIdentifier(): Int {
        return id;
    }
}