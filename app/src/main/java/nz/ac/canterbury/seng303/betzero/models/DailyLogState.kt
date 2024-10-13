package nz.ac.canterbury.seng303.betzero.models

class DailyLogState (
    val id: Int,
    val date: String,
    val loggedToday: Boolean
    ) : Identifiable {
        override fun getIdentifier(): Int {
            return id;
        }
    }