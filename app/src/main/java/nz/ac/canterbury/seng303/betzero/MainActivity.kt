package nz.ac.canterbury.seng303.betzero

import AnalyticsScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.BetzeroTheme
import nz.ac.canterbury.seng303.betzero.screens.CalendarScreen
import nz.ac.canterbury.seng303.betzero.screens.EmergencyScreen
import nz.ac.canterbury.seng303.betzero.screens.GettingStartedScreen
import nz.ac.canterbury.seng303.betzero.screens.InitialScreen
import nz.ac.canterbury.seng303.betzero.screens.OnboardingScreen
import nz.ac.canterbury.seng303.betzero.screens.PreferencesScreen
import nz.ac.canterbury.seng303.betzero.screens.SummariesScreen
import nz.ac.canterbury.seng303.betzero.screens.UpdateUserProfileScreen
import nz.ac.canterbury.seng303.betzero.screens.UserProfileScreen
import nz.ac.canterbury.seng303.betzero.viewmodels.PreferencesViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val preferencesViewModel: PreferencesViewModel by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //set whether the system is in dark mode as it must come from a composable
            preferencesViewModel.setIsSystemInDarkTheme(isSystemInDarkTheme())


            val isDarkTheme by preferencesViewModel.isDarkTheme.collectAsStateWithLifecycle()


            BetzeroTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val iconModifier = Modifier.size(50.dp)
                val iconColor = MaterialTheme.colorScheme.primary

                Scaffold(
                    topBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        TopAppBar(
                            title = { Text("BetZero") },
                            actions = {
                                if (currentDestination?.route !in listOf("OnBoardingScreen", "GettingStartedScreen")) {
                                    IconButton(onClick = { navController.navigate("userProfileScreen") }) {
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

                        if (currentDestination?.route !in listOf("OnBoardingScreen", "GettingStartedScreen")) {
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
                                            tint = iconColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        NavHost(navController = navController, startDestination = "InitialScreen") {
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
                                SummariesScreen(navController = navController)
                            }
                            composable("EmergencyScreen") {
                                EmergencyScreen(navController = navController)
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


@Composable
fun Home(navController: NavController, viewModel: PreferencesViewModel = koinViewModel()) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Main Screen")
    }
}