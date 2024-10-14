package nz.ac.canterbury.seng303.betzero.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.betzero.models.RelapseLog
import nz.ac.canterbury.seng303.betzero.models.UserProfile
import nz.ac.canterbury.seng303.betzero.utils.CalendarUtil.getMonthName
import nz.ac.canterbury.seng303.betzero.utils.CalendarUtil.stripTime
import nz.ac.canterbury.seng303.betzero.utils.InputValidation
import nz.ac.canterbury.seng303.betzero.utils.UserUtil
import nz.ac.canterbury.seng303.betzero.viewmodels.CalendarViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

@Composable
fun CalendarScreen(navController: NavController, viewModel: CalendarViewModel = koinViewModel()) {
    val userProfile by viewModel.userProfile.collectAsState()
    val relapseLogs by viewModel.relapseLogs.collectAsState()
    val startDate = userProfile?.lastGambledDate ?: Date()
    val streakDays = UserUtil.getAllDatesSinceStart(startDate)

    var showModal by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "You have been gamble free for ${streakDays.size - 1} ${if (streakDays.size - 1 == 1) "day!" else "days!"}",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Divider(Modifier.padding(16.dp))

        Button(
            onClick = { showModal = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Text("Log Relapse", color = Color.White, fontSize = 16.sp)
        }

        if (showModal) {
            userProfile?.let {
                ShowRelapseForm(
                    userProfile = it,
                    viewModel = viewModel,
                    onDismiss = { showModal = false }
                )
            }
        }

        Divider(Modifier.padding(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            CustomCalendar(
                streakDays = streakDays,
                relapseLogs = relapseLogs,
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun ShowRelapseForm(
    userProfile: UserProfile,
    viewModel: CalendarViewModel,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf("") }
    var amountSpent by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf<String?>(null) }
    var amountSpentError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = dateFormat.format(selectedCalendar.time)
            dateError = if (InputValidation.validateDate(selectedDate)) null else "Please select a valid date."
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.7f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Log relapse",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("When did you relapse?") },
                    enabled = false,
                    trailingIcon = {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = "Select relapse date",
                            modifier = Modifier.clickable { datePickerDialog.show() }
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledContainerColor = Color.Transparent,
                        disabledBorderColor = Color.Black,
                        disabledLabelColor = Color.Black,
                        disabledTrailingIconColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    isError = dateError != null
                )

                if (dateError != null) {
                    Text(
                        text = dateError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amountSpent,
                    onValueChange = {
                        amountSpent = it
                        amountSpentError = if (InputValidation.validateTotalSpent(amountSpent)) null else "Please enter a valid non-negative number."
                    },
                    label = { Text("How much did you spend?") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = amountSpentError != null
                )

                if (amountSpentError != null) {
                    Text(
                        text = amountSpentError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier.width(120.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Cancel", color = Color.White, fontSize = 16.sp)
                    }

                    Button(
                        onClick = {
                            val parsedAmount = amountSpent.toDouble()
                            val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate)

                            viewModel.updateUserProfile(
                                id = userProfile.id,
                                name = userProfile.name,
                                age = userProfile.age,
                                totalSpent = userProfile.totalSpent + parsedAmount,
                                gamblingStartDate = userProfile.gamblingStartDate,
                                lastGambledDate = if (parsedDate.after(userProfile.lastGambledDate)) parsedDate else userProfile.lastGambledDate,
                                notificationTime = LocalTime.NOON
                            )
                            viewModel.insertRelapseLog(selectedDate, amountSpent)

                            onDismiss()
                        },
                        modifier = Modifier.width(120.dp),
                        enabled = (selectedDate.isNotEmpty() && amountSpent.isNotEmpty() && dateError == null && amountSpentError == null),
                        colors = ButtonDefaults.buttonColors(containerColor = if (dateError == null && amountSpentError == null) Color.Green else Color.Gray)
                    ) {
                        Text("Submit", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomCalendar(
    streakDays: List<Date>,
    relapseLogs: List<RelapseLog>,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel
) {
    val calendar = rememberSaveable { Calendar.getInstance() }

    var displayedMonth by rememberSaveable { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var displayedYear by rememberSaveable { mutableStateOf(calendar.get(Calendar.YEAR)) }

    LaunchedEffect(displayedMonth, displayedYear) {
        calendar.set(Calendar.MONTH, displayedMonth)
        calendar.set(Calendar.YEAR, displayedYear)
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "<",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        calendar.add(Calendar.MONTH, -1)
                        displayedMonth = calendar.get(Calendar.MONTH)
                        displayedYear = calendar.get(Calendar.YEAR)
                    },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${getMonthName(displayedMonth)} $displayedYear",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            Text(
                text = ">",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        calendar.add(Calendar.MONTH, 1)
                        displayedMonth = calendar.get(Calendar.MONTH)
                        displayedYear = calendar.get(Calendar.YEAR)
                    },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            daysOfWeek.forEach { day ->
                Text(text = day, modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        CalendarDatesGrid(
            streakDays = streakDays,
            calendar = calendar,
            relapseLogs = relapseLogs,
            viewModel = viewModel
        )
    }
}

@Composable
fun CalendarDatesGrid(
    streakDays: List<Date>,
    calendar: Calendar,
    relapseLogs: List<RelapseLog>,
    viewModel: CalendarViewModel
) {
    val normalizedStreakDays = streakDays.map { stripTime(it) }

    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    val weeks = (firstDayOfWeek + daysInMonth + 6) / 7

    var selectedDate by remember { mutableStateOf<Date?>(null) }

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Column {
        var dayCounter = 1

        for (week in 0 until weeks) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (dayOfWeek in 0..6) {
                    if (week == 0 && dayOfWeek < firstDayOfWeek || dayCounter > daysInMonth) {
                        Box(modifier = Modifier
                            .weight(1f)
                            .height(40.dp))
                    } else {
                        val date = Calendar.getInstance().apply {
                            set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                            set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                            set(Calendar.DAY_OF_MONTH, dayCounter)
                        }.time

                        val normalizedDate = stripTime(date)

                        val dateStr = dateFormatter.format(normalizedDate)
                        val hasRelapse = relapseLogs.any { it.date == dateStr }

                        Box(modifier = Modifier.weight(1f)) {
                            DayBox(
                                day = dayCounter,
                                isStreakDay = normalizedStreakDays.contains(normalizedDate),
                                hasRelapse = hasRelapse,
                                onClick = {
                                    selectedDate = date
                                }
                            )
                        }
                        dayCounter++
                    }
                }
            }
        }

        if (selectedDate != null) {
            val selectedDateStr = dateFormatter.format(selectedDate!!)
            val filteredRelapseLogs = relapseLogs.filter { it.date == selectedDateStr }

            ShowDayDetails(
                date = selectedDate!!,
                relapseLogs = filteredRelapseLogs,
                onDismiss = { selectedDate = null },
                onDeleteLog = { relapseLog ->
                    viewModel.deleteRelapseLog(relapseLog)
                }
            )
        }
    }
}

@Composable
fun ShowDayDetails(date: Date, relapseLogs: List<RelapseLog>, onDismiss: () -> Unit, onDeleteLog: (RelapseLog) -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.6f)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.surface,)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(date),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                )

                if (relapseLogs.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        items(relapseLogs) { relapse ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .background(Color.Red.copy(alpha = 0.2f))
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "Relapse Date: ${relapse.date}", color = Color.Red)
                                        Text(text = "Amount Spent: \$${relapse.amountSpent}", color = Color.Red)
                                    }

                                    IconButton(
                                        onClick = { onDeleteLog(relapse) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete relapse log",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "No logs available for this day",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.width(120.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Close")
                    }
                }
            }
        }
    }
}


@Composable
fun DayBox(
    day: Int,
    isStreakDay: Boolean,
    hasRelapse: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        hasRelapse -> Color(0xFFFF5252)
        isStreakDay -> Color(0xFF50C878)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$day", fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
