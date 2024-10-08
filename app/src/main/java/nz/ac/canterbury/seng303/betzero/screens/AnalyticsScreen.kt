import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.betzero.viewmodels.AnalyticsViewModel
import nz.ac.canterbury.seng303.betzero.utils.UserUtil
import org.koin.androidx.compose.koinViewModel
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(navController: NavController, viewModel: AnalyticsViewModel = koinViewModel()) {
    val userProfile by viewModel.userProfile.collectAsState()

    val overallSavings = userProfile?.totalSaved ?: 0.00
    val moneySpentPerDay = userProfile?.dailySavings ?: 0.00
    val daysSinceStart = userProfile?.let {
        val currentDate = Date()
        UserUtil.calculateDaysBetween(it.gamblingStartDate, currentDate)
    } ?: 0
    val (projectedMonthlySavings, projectedYearlySavings) = UserUtil.calculateProjectedSavings(moneySpentPerDay)
    val startDate = userProfile?.gamblingStartDate?.toString() ?: "N/A"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Overall savings", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Text(
            text = "$${String.format(Locale.US, "%.2f", overallSavings)}",
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        InfoRow(title = "Money spent per day", value = "$${String.format(Locale.US, "%.2f", moneySpentPerDay)}")

        InfoRow(
            title = "Days since start date",
            value = "$startDate",
            rightValue = "$daysSinceStart ${if (daysSinceStart == 1L) "day" else "days"}"
        )

        InfoRow(
            title = "Overall saved",
            value = "$${String.format(Locale.US, "%.2f", overallSavings)} × $daysSinceStart days",
            rightValue = "$${String.format(Locale.US, "%.2f", overallSavings * daysSinceStart)}"
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text(text = "Projected savings", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        InfoRow(title = "Projected monthly savings", value = "$${String.format(Locale.US, "%.2f", projectedMonthlySavings)}")

        InfoRow(title = "Projected yearly savings", value = "$${String.format(Locale.US, "%.2f", projectedYearlySavings)}")
    }
}

@Composable
fun InfoRow(title: String, value: String, rightValue: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 16.sp)
        rightValue?.let {
            Text(text = it, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        if (rightValue == null) {
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
