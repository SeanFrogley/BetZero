package nz.ac.canterbury.seng303.betzero.screens

import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth

@Composable
fun GettingStartedScreen(navController: NavController, viewModel: GettingStartedViewModel = koinViewModel()) {
    var userName by rememberSaveable { mutableStateOf("") }
    var totalSpent by rememberSaveable { mutableStateOf("") }
    var selectedStartDate by rememberSaveable { mutableStateOf("") }
    var selectedLastGambledDate by rememberSaveable { mutableStateOf("") }

    var nameError by rememberSaveable { mutableStateOf<String?>(null) }
    var totalSpentError by rememberSaveable { mutableStateOf<String?>(null) }
    var startDateError by rememberSaveable { mutableStateOf<String?>(null) }
    var lastGambledDateError by rememberSaveable { mutableStateOf<String?>(null) }

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val datePickerDialogStart = DatePickerDialog(
        LocalContext.current,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            selectedStartDate = dateFormat.format(selectedCalendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val datePickerDialogLastGambled = DatePickerDialog(
        LocalContext.current,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            selectedLastGambledDate = dateFormat.format(selectedCalendar.time)
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
                .verticalScroll(rememberScrollState())
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

            OutlinedTextField(
                value = userName,
                onValueChange = {
                    userName = it
                    nameError = if (InputValidation.validateUsersName(userName)) null else "Please enter a name that only consists of letters, -, or '."
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

            OutlinedTextField(
                value = totalSpent,
                onValueChange = {
                    totalSpent = it
                    totalSpentError = if (InputValidation.validateTotalSpent(totalSpent)) null else "Please enter a valid non-negative number."
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

            OutlinedTextField(
                value = selectedStartDate,
                onValueChange = { },
                readOnly = true,
                label = { Text("When did you start gambling?") },
                trailingIcon = {
                    IconButton(onClick = { datePickerDialogStart.show() }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Select start date")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            startDateError = if (selectedStartDate.isNotEmpty() && !InputValidation.validateDate(selectedStartDate)) {
                "Please enter a valid start date that isn't in the future."
            } else if (selectedStartDate.isNotEmpty() && selectedLastGambledDate.isNotEmpty()
                && !InputValidation.validateStartAndLastGambledDate(selectedStartDate, selectedLastGambledDate)) {
                "The start date should be before or the same as the last gambled date."
            } else {
                null
            }
            if (startDateError != null) {
                Text(
                    text = startDateError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = selectedLastGambledDate,
                onValueChange = { },
                readOnly = true,
                label = { Text("When did you last gamble?") },
                trailingIcon = {
                    IconButton(onClick = { datePickerDialogLastGambled.show() }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Select last gambled date")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            lastGambledDateError = if (selectedLastGambledDate.isNotEmpty() && !InputValidation.validateDate(selectedLastGambledDate)) {
                "Please enter a valid last gambled date that isn't in the future."
            } else if (selectedLastGambledDate.isNotEmpty() && selectedLastGambledDate.isNotEmpty()
                && !InputValidation.validateStartAndLastGambledDate(selectedStartDate, selectedLastGambledDate)) {
                "The start date should be before or the same as the last gambled date."
            } else {
                null
            }
            if (lastGambledDateError != null) {
                Text(
                    text = lastGambledDateError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (nameError == null && totalSpentError == null && startDateError == null && lastGambledDateError == null) {
                        viewModel.saveUserProfile(
                            name = userName,
                            totalSpent = totalSpent.toDouble(),
                            gamblingStartDate = SimpleDateFormat("yyyy-MM-dd").parse(selectedStartDate),
                            lastGambledDate = SimpleDateFormat("yyyy-MM-dd").parse(selectedLastGambledDate)
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
                enabled = (userName.isNotEmpty() && totalSpent.isNotEmpty() && selectedStartDate.isNotEmpty() && selectedLastGambledDate.isNotEmpty())
                        && (nameError == null && totalSpentError == null && startDateError == null && lastGambledDateError == null)
            ) {
                Text(text = "Submit", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}