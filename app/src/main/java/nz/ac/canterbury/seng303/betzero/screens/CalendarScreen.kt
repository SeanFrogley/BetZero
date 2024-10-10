package nz.ac.canterbury.seng303.betzero.screens

import android.widget.CalendarView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@Composable
fun CalendarScreen(navController: NavController) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            CalendarView(context)
        }
    )
}