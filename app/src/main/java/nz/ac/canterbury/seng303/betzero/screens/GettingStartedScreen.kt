package nz.ac.canterbury.seng303.betzero.screens

import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.betzero.utils.InputValidation
import nz.ac.canterbury.seng303.betzero.viewmodels.GettingStartedViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GettingStartedScreen(navController: NavController, viewModel: GettingStartedViewModel = koinViewModel()) {
    var userName by remember { mutableStateOf("") }
    var totalSpent by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }

    // error messages state
    var nameError by remember { mutableStateOf<String?>(null) }
    var totalSpentError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    // date picker setup
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            selectedDate = dateFormat.format(selectedCalendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Let's get started!",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Can you answer some questions for us?",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 14.sp,
                            color = Color.Gray
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // input for user's name
            OutlinedTextField(
                value = userName,
                onValueChange = {
                    userName = it
                    nameError = if (InputValidation.validateUsersName(userName)) null else "Invalid name. No special characters allowed."
                },
                label = { Text("What's your name?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
                singleLine = true,
                isError = nameError != null
            )
            if (nameError != null) {
                Text(
                    text = nameError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // input for total money spent
            OutlinedTextField(
                value = totalSpent,
                onValueChange = {
                    totalSpent = it
                    totalSpentError = if (InputValidation.validateTotalSpent(totalSpent)) null else "Invalid input. Enter a valid non-negative number."
                },
                label = { Text("How much money have you spent?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = totalSpentError != null
            )
            if (totalSpentError != null) {
                Text(
                    text = totalSpentError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // date selector for when they began gambling
            Button(
                onClick = { datePickerDialog.show() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4AB7D6)),
                shape = MaterialTheme.shapes.medium,
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text(
                    text = if (selectedDate.isEmpty()) "When did you start gambling?" else "Started Gambling: $selectedDate",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            dateError = if (selectedDate.isNotEmpty() && !InputValidation.validateDate(selectedDate)) {
                "Invalid date. The selected date cannot be in the future."
            } else {
                null
            }
            if (dateError != null) {
                Text(
                    text = dateError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // submit button
            Button(
                onClick = {
                    if (nameError == null && totalSpentError == null && dateError == null) {
                        viewModel.saveUserProfile(
                            name = userName,
                            totalSpent = totalSpent.toDouble(),
                            totalSaved = 0.0,
                            startDate = SimpleDateFormat("yyyy-MM-dd").parse(selectedDate)
                        )
                        navController.navigate("Home")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4AB7D6)),
                shape = MaterialTheme.shapes.medium,
                elevation = ButtonDefaults.buttonElevation(8.dp),
                enabled = (userName.isNotEmpty() && totalSpent.isNotEmpty() && selectedDate.isNotEmpty()) && (nameError == null && totalSpentError == null && dateError == null)
            ) {
                Text(text = "Submit", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
