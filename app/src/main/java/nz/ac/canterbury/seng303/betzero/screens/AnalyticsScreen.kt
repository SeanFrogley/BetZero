package nz.ac.canterbury.seng303.betzero.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.betzero.viewmodels.AnalyticsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnalyticsScreen(navController: NavController, viewModel: AnalyticsViewModel = koinViewModel()) {
    val overallSavings = 20.00
    val moneySpentPerDay = 20.00
    val daysSinceStart = 1
    val projectedMonthlySavings = 608.33
    val projectedYearlySavings = 7300.00
    val startDate = "Oct 7, 2024"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Overall savings", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Text(
            text = "$${String.format("%.2f", overallSavings)}",
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        InfoRow(title = "Money spent per day", value = "$${String.format("%.2f", moneySpentPerDay)}")

        InfoRow(title = "Days since start date", value = "$startDate", rightValue = "$daysSinceStart day")

        InfoRow(
            title = "Overall saved",
            value = "$${String.format("%.2f", overallSavings)} × $daysSinceStart day",
            rightValue = "$${String.format("%.2f", overallSavings * daysSinceStart)}"
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text(text = "Projected savings", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        InfoRow(title = "Projected monthly savings", value = "$${String.format("%.2f", projectedMonthlySavings)}")

        InfoRow(title = "Projected yearly savings", value = "$${String.format("%.2f", projectedYearlySavings)}")
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
