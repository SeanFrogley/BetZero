package nz.ac.canterbury.seng303.betzero

import AnalyticsScreen
import HomeScreen
import SummariesScreen
import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.BetzeroTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import nz.ac.canterbury.seng303.betzero.screens.CalendarScreen
import nz.ac.canterbury.seng303.betzero.screens.EmergencyScreen
import nz.ac.canterbury.seng303.betzero.screens.GettingStartedScreen
import nz.ac.canterbury.seng303.betzero.screens.InitialScreen
import nz.ac.canterbury.seng303.betzero.screens.OnboardingScreen
import nz.ac.canterbury.seng303.betzero.screens.PreferencesScreen
import nz.ac.canterbury.seng303.betzero.screens.UpdateUserProfileScreen
import nz.ac.canterbury.seng303.betzero.screens.UserProfileScreen
import nz.ac.canterbury.seng303.betzero.utils.AlarmUtil
import nz.ac.canterbury.seng303.betzero.utils.UserUtil
import nz.ac.canterbury.seng303.betzero.viewmodels.PreferencesViewModel
import org.koin.android.ext.android.inject
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val preferencesViewModel: PreferencesViewModel by inject()

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //set whether the system is in dark mode as it must come from a composable
            preferencesViewModel.setIsSystemInDarkTheme(isSystemInDarkTheme())

            val userProfile by preferencesViewModel.userProfile.collectAsState()
            var notificationTime by rememberSaveable { mutableStateOf(LocalTime.NOON) }
            val isDarkTheme by preferencesViewModel.isDarkTheme.collectAsStateWithLifecycle()
            val context = LocalContext.current

            val postNotificationPermission =
                rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

            LaunchedEffect(key1 = true) {
                if (!postNotificationPermission.status.isGranted) {
                    postNotificationPermission.launchPermissionRequest()
                }
            }
            LaunchedEffect(userProfile) {
                userProfile?.let {
                    notificationTime = it.notificationTime
                }
            }

            Column {
                BetzeroTheme(darkTheme = isDarkTheme) {
                    BetzeroTheme {
                        val navController = rememberNavController()
                        val iconModifier = Modifier.size(50.dp)
                        val iconColor = MaterialTheme.colorScheme.primary
                        setAlarm(context, notificationTime)

                        Scaffold(
                            topBar = {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentDestination = navBackStackEntry?.destination
                                TopAppBar(
                                    title = { Text("BetZero") },
                                    actions = {
                                        if (currentDestination?.route !in listOf(
                                                "OnBoardingScreen",
                                                "GettingStartedScreen"
                                            )
                                        ) {
                                            IconButton(onClick = { navController.navigate("UserProfileScreen") }) {
                                                Icon(
                                                    imageVector = Icons.Default.AccountCircle,
                                                    contentDescription = "Profile",
                                                    modifier = iconModifier,
                                                    tint = iconColor
                                                )
                                            }
                                        }
                                    }
                                )
                            },
                            bottomBar = {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentDestination = navBackStackEntry?.destination

                            if (currentDestination?.route !in listOf(
                                    "OnBoardingScreen",
                                    "GettingStartedScreen"
                                )
                            ) {
                                BottomAppBar(
                                    modifier = Modifier.height(60.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        IconButton(onClick = { navController.navigate("CalendarScreen") }) {
                                            Icon(
                                                imageVector = Icons.Default.CalendarMonth,
                                                contentDescription = "Calendar",
                                                modifier = iconModifier,
                                                tint = iconColor
                                            )
                                        }
                                        IconButton(onClick = { navController.navigate("AnalyticsScreen") }) {
                                            Icon(
                                                imageVector = Icons.Default.AttachMoney,
                                                contentDescription = "Analytics",
                                                modifier = iconModifier,
                                                tint = iconColor
                                            )
                                        }
                                        IconButton(onClick = { navController.navigate("Home") }) {
                                            Icon(
                                                imageVector = Icons.Default.Home,
                                                contentDescription = "Home",
                                                modifier = iconModifier,
                                                tint = iconColor
                                            )
                                        }
                                        IconButton(onClick = { navController.navigate("SummariesScreen") }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.List,
                                                contentDescription = "Summaries",
                                                modifier = iconModifier,
                                                tint = iconColor
                                            )
                                        }
                                        IconButton(onClick = { navController.navigate("EmergencyScreen") }) {
                                            Icon(
                                                imageVector = Icons.Default.Sos,
                                                contentDescription = "SOS",
                                                modifier = iconModifier,
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    ) {
                        Box(modifier = Modifier.padding(it)) {
                            NavHost(
                                navController = navController,
                                startDestination = "InitialScreen"
                            ) {
                                composable("InitialScreen") {
                                    InitialScreen(navController = navController)
                                }
                                composable("OnBoardingScreen") {
                                    OnboardingScreen(navController = navController)
                                }
                                composable("CalendarScreen") {
                                    CalendarScreen(navController = navController)
                                }
                                composable("AnalyticsScreen") {
                                    AnalyticsScreen(navController = navController)
                                }
                                composable("Home") {
                                    Home(navController = navController)
                                }
                                composable("SummariesScreen") {
                                    SummariesScreen()
                                }
                                composable("EmergencyScreen") {
                                    EmergencyScreen()
                                }
                                composable("GettingStartedScreen") {
                                    GettingStartedScreen(navController = navController)
                                }
                                composable("UserProfileScreen") {
                                    UserProfileScreen(navController = navController)
                                }
                                composable("UpdateUserProfileScreen") {
                                    UpdateUserProfileScreen(navController = navController)
                                }
                                composable("PreferencesScreen") {
                                    PreferencesScreen(navController = navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
    //set the notification alarm
    private fun setAlarm(context: Context, notificationTime: LocalTime ) {
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmUtil::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val triggerTime = UserUtil.convertLocalTimeToMillis(notificationTime)
        val interval = 0L
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, interval, pendingIntent)
    }
    }
