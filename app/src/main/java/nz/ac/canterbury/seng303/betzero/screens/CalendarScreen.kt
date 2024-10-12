package nz.ac.canterbury.seng303.betzero.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.betzero.utils.UserUtil
import nz.ac.canterbury.seng303.betzero.viewmodels.AnalyticsViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar
import java.util.Date
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(navController: NavController, viewModel: AnalyticsViewModel = koinViewModel()) {
    val userProfile by viewModel.userProfile.collectAsState()

    val startDate = userProfile?.lastGambledDate ?: Date()

    val streakDays = UserUtil.getAllDatesSinceStart(startDate)

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        CustomCalendar(streakDays = streakDays, modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun CustomCalendar(
    streakDays: List<Date>,
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${getMonthName(displayedMonth)} $displayedYear",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                Text(text = "<", modifier = Modifier.padding(8.dp).clickable {
                    calendar.add(Calendar.MONTH, -1)
                    displayedMonth = calendar.get(Calendar.MONTH)
                    displayedYear = calendar.get(Calendar.YEAR)
                })
                Text(text = ">", modifier = Modifier.padding(8.dp).clickable {
                    calendar.add(Calendar.MONTH, 1)
                    displayedMonth = calendar.get(Calendar.MONTH)
                    displayedYear = calendar.get(Calendar.YEAR)
                })
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            daysOfWeek.forEach { day ->
                Text(text = day, modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        CalendarDatesGrid(
            streakDays = streakDays,
            calendar = calendar
        )
    }
}

@Composable
fun CalendarDatesGrid(streakDays: List<Date>, calendar: Calendar) {
    val normalizedStreakDays = streakDays.map { stripTime(it) }

    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    val weeks = (firstDayOfWeek + daysInMonth + 6) / 7

    Column {
        var dayCounter = 1

        for (week in 0 until weeks) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (dayOfWeek in 0..6) {
                    if (week == 0 && dayOfWeek < firstDayOfWeek || dayCounter > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).height(40.dp))
                    } else {
                        val date = Calendar.getInstance().apply {
                            set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                            set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                            set(Calendar.DAY_OF_MONTH, dayCounter)
                        }.time

                        val normalizedDate = stripTime(date)

                        Box(modifier = Modifier.weight(1f)) {
                            DayBox(
                                day = dayCounter,
                                isStreakDay = normalizedStreakDays.contains(normalizedDate)
                            )
                        }

                        dayCounter++
                    }
                }
            }
        }
    }
}


@Composable
fun DayBox(day: Int, isStreakDay: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(if (isStreakDay) Color(0xFF4CAF50) else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$day", fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}


fun getMonthName(month: Int): String {
    val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
    val cal = Calendar.getInstance().apply {
        set(Calendar.MONTH, month)
    }
    return monthFormat.format(cal.time)
}

fun stripTime(date: Date): Date {
    val calendar = Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.time
}