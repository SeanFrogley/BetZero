import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.betzero.utils.UserUtil
import nz.ac.canterbury.seng303.betzero.viewmodels.AnalyticsViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(navController: NavController, viewModel: AnalyticsViewModel = koinViewModel()) {
    val userProfile by viewModel.userProfile.collectAsState()
    val overallSavings = userProfile?.totalSaved ?: 0.00
    val dailySavings = userProfile?.dailySavings ?: 0.00
    val projectedMonthlySavings = UserUtil.calculateProjectedSavings(dailySavings).first
    val projectedYearlySavings = UserUtil.calculateProjectedSavings(dailySavings).second

    val daysSinceStart = userProfile?.let {
        val currentDate = Date()
        UserUtil.calculateDaysBetween(it.lastGambledDate, currentDate)
    } ?: 0
    var isMonthlyView by rememberSaveable { mutableStateOf(false) }

    val size = if (isMonthlyView) 30 else 7

    val savingsData = UserUtil.createSavingsData(overallSavings, dailySavings, size)
    val dateList = UserUtil.createDateList(size)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(text = "Weekly", fontSize = 16.sp)
            Switch(
                checked = isMonthlyView,
                onCheckedChange = { isMonthlyView = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor =MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.primary,

                    checkedTrackColor = MaterialTheme.colorScheme.onPrimary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            Text(text = "Monthly", fontSize = 16.sp)
        }

        BarChart(
            barData = savingsData,
            dateList = dateList,
            dailySavings = dailySavings
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        InfoRow(title = "Money saved per day", value = "$${String.format(Locale.US, "%.2f", dailySavings)}")

        InfoRow(
            title = "Days since start date",
            value = "$daysSinceStart",
            rightValue = "$daysSinceStart ${if (daysSinceStart == 1L) "day" else "days"}"
        )

        InfoRow(
            title = "Overall saved",
            value = "$${String.format(Locale.US, "%.2f", dailySavings)} × $daysSinceStart days",
            rightValue = "$${String.format(Locale.US, "%.2f", dailySavings * daysSinceStart)}"
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

@Composable
fun BarChart(
    barData: List<Float>,
    dateList: List<Date>,
    dailySavings: Double,
    barColor: Color = Color(0xFF4CAF50),
    selectedBarColor: Color = Color(0xFF1E88E5)
) {
    val maxBarHeight = 300f
    val padding = 4.dp
    val spacing = 20f
    val maxValue = barData.maxOrNull() ?: 1f

    var selectedBarIndex by remember { mutableStateOf(-1) }
    var showPopup by remember { mutableStateOf(false) }
    var popupPosition by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(padding)
                .pointerInput(barData.size) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val press = event.changes.firstOrNull()

                            press?.let {
                                if (press.pressed) {
                                    val totalAvailableWidth = size.width
                                    val barWidth = (totalAvailableWidth - (barData.size - 1) * spacing) / barData.size

                                    barData.forEachIndexed { index, _ ->
                                        val barXStart = index * (barWidth + spacing)
                                        val barXEnd = barXStart + barWidth

                                        if (press.position.x in barXStart..barXEnd) {
                                            selectedBarIndex = index
                                            popupPosition = press.position
                                            showPopup = true
                                        }
                                    }
                                } else {
                                    showPopup = false
                                    selectedBarIndex = -1
                                }
                            }
                            if (!press?.pressed!!) {
                                showPopup = false
                                selectedBarIndex = -1
                            }
                        }
                    }
                }
        ) {
            val totalAvailableWidth = size.width
            val barWidth = (totalAvailableWidth - (barData.size - 1) * spacing) / barData.size

            barData.forEachIndexed { index, value ->
                val barHeight = (value / maxValue) * maxBarHeight

                val currentBarColor = if (index == selectedBarIndex) selectedBarColor else barColor

                drawRoundRect(
                    color = currentBarColor,
                    topLeft = Offset(x = (index * (barWidth + spacing)), y = size.height - barHeight),
                    size = androidx.compose.ui.geometry.Size(width = barWidth, height = barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f)
                )
            }
        }

        if (showPopup && selectedBarIndex != -1) {
            val selectedValue = barData[selectedBarIndex]
            val selectedDate = dateList[selectedBarIndex]
            val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.US)

            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(popupPosition.x.toInt(), popupPosition.y.toInt())
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "${dateFormatter.format(selectedDate)}\nSaved: $${String.format(Locale.US, "%.2f", selectedValue)}",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}