package nz.ac.canterbury.seng303.betzero.models

class RelapseLog (
    val id: Int,
    val date: String,
    val amountSpent: Double
) : Identifiable {
    override fun getIdentifier(): Int {
        return id;
    }
}