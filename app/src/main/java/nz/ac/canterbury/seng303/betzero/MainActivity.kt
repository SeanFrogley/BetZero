package nz.ac.canterbury.seng303.betzero

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.BetzeroTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import nz.ac.canterbury.seng303.betzero.screens.AnalyticsScreen
import nz.ac.canterbury.seng303.betzero.screens.CalendarScreen
import nz.ac.canterbury.seng303.betzero.screens.EmergencyScreen
import nz.ac.canterbury.seng303.betzero.screens.GettingStartedScreen
import nz.ac.canterbury.seng303.betzero.screens.InitialScreen
import nz.ac.canterbury.seng303.betzero.screens.OnboardingScreen
import nz.ac.canterbury.seng303.betzero.screens.SummariesScreen
import nz.ac.canterbury.seng303.betzero.viewmodels.InitialViewModel
import org.koin.androidx.compose.koinViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userProfile")

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userProfileExists = runBlocking {
            val userProfileKey = stringPreferencesKey("userProfile")
            val preferences = dataStore.data.first()
            val userProfileJson = preferences[userProfileKey]
            !userProfileJson.isNullOrEmpty()
        }

        val startDestination = if (userProfileExists) "Home" else "OnBoardingScreen"

        setContent {
            BetzeroTheme {
                val navController = rememberNavController()
                val iconModifier = Modifier.size(50.dp)
                val iconColor = MaterialTheme.colorScheme.primary
                val initialViewModel: InitialViewModel = koinViewModel()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("BetZero") },
                            actions = {
                                IconButton(onClick = { /* take user to user profile page*/ }) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Profile",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
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
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Home(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Main Screen")
    }
}