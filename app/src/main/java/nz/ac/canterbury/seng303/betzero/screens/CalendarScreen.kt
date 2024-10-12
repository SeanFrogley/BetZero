package nz.ac.canterbury.seng303.betzero.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.betzero.models.DailyLog
import nz.ac.canterbury.seng303.betzero.utils.CalendarUtil.getMonthName
import nz.ac.canterbury.seng303.betzero.utils.CalendarUtil.stripTime
import nz.ac.canterbury.seng303.betzero.utils.UserUtil
import nz.ac.canterbury.seng303.betzero.viewmodels.CalendarViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(navController: NavController, viewModel: CalendarViewModel = koinViewModel()) {
    val userProfile by viewModel.userProfile.collectAsState()
    val dailyLogs = listOf(
        DailyLog(id = 1, feeling = "Happy", voiceMemo = "path/to/memo1", date = "2024-10-09"),
        DailyLog(id = 2, feeling = "Sad", voiceMemo = "path/to/memo2", date = "2024-10-10"),
        DailyLog(id = 3, feeling = "Neutral", voiceMemo = "path/to/memo3", date = "2024-10-11")
    )
    val startDate = userProfile?.lastGambledDate ?: Date()
    val streakDays = UserUtil.getAllDatesSinceStart(startDate)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Current Streak: ${streakDays.size} days",
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Divider()

        Box(modifier = Modifier.fillMaxSize()) {
            CustomCalendar(
                streakDays = streakDays,
                dailyLogs = dailyLogs,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun CustomCalendar(
    streakDays: List<Date>,
    dailyLogs: List<DailyLog>,
    modifier: Modifier = Modifier
) {
    val calendar = remember { Calendar.getInstance() }

    var displayedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var displayedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }

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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
            dailyLogs = dailyLogs
        )
    }
}


@Composable
fun CalendarDatesGrid(streakDays: List<Date>, calendar: Calendar, dailyLogs: List<DailyLog>) {
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
                        val hasLog = dailyLogs.any { it.date == dateStr }

                        Box(modifier = Modifier.weight(1f)) {
                            DayBox(
                                day = dayCounter,
                                isStreakDay = normalizedStreakDays.contains(normalizedDate),
                                hasLog = hasLog,
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
            val filteredLogs = dailyLogs.filter { it.date == selectedDateStr }
            ShowDayDetails(date = selectedDate!!, logs = filteredLogs, onDismiss = { selectedDate = null })
        }
    }
}

@Composable
fun ShowDayDetails(date: Date, logs: List<DailyLog>, onDismiss: () -> Unit) {
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
                .background(Color.White)
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

                if (logs.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        items(logs) { entry ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .background(Color.LightGray)
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text(text = "Date: ${entry.date}")
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val moodIcon = when (entry.feeling) {
                                            "Happy" -> Icons.Default.SentimentVerySatisfied
                                            "Neutral" -> Icons.Default.SentimentNeutral
                                            "Sad" -> Icons.Default.SentimentVeryDissatisfied
                                            else -> Icons.Default.SentimentNeutral
                                        }
                                        Icon(
                                            imageVector = moodIcon,
                                            contentDescription = entry.feeling
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(onClick = {
                                        }) {
                                            Text(text = "Play")
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(text = "No logs available for this day", modifier = Modifier.align(Alignment.CenterHorizontally))
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
fun DayBox(day: Int, isStreakDay: Boolean, hasLog: Boolean, onClick: () -> Unit) {
    val backgroundColor = when {
        hasLog -> Color.Green
        isStreakDay -> Color.Yellow
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