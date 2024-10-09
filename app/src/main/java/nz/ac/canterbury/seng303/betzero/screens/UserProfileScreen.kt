package nz.ac.canterbury.seng303.betzero.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.betzero.utils.UserUtil
import nz.ac.canterbury.seng303.betzero.viewmodels.UserProfileViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

@Composable
fun UserProfileScreen(navController: NavController, viewModel: UserProfileViewModel = koinViewModel()) {
    val userProfile by viewModel.userProfile.collectAsState()
    val dateFormatter = SimpleDateFormat("MMM-dd-yyyy", Locale.US)
    val currentDate = Date()
    val currentStreak = userProfile?.let {
        UserUtil.calculateDaysBetween(it.lastGambledDate, currentDate)
    } ?: 0

    val startDate = userProfile?.gamblingStartDate?.let { dateFormatter.format(it) } ?: "N/A"
    val lastGambledDate = userProfile?.lastGambledDate?.let { dateFormatter.format(it) } ?: "N/A"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "User Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        InfoRow(title = "Name", value = userProfile?.name ?: "N/A")

        InfoRow(title = "You Began Gambling", value = startDate)

        InfoRow(title = "You Last Gambled", value = lastGambledDate)

        InfoRow(
            title = "Current Streak",
            value = "$currentStreak ${if (currentStreak == 1L) "day" else "days"}"
        )

        // Add the button here
        Button(
            onClick = { navController.navigate("UpdateUserProfileScreen")},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(text = "Update User Information", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun InfoRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 16.sp)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

