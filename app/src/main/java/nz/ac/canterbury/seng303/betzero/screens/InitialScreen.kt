package nz.ac.canterbury.seng303.betzero.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.betzero.viewmodels.InitialViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun InitialScreen(navController: NavController, viewModel: InitialViewModel = koinViewModel()) {
    val userProfileExists by viewModel.userProfileExists.collectAsState()

    when (userProfileExists) {
        null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        true -> {
            LaunchedEffect(Unit) {
                navController.navigate("Home") {
                    popUpTo("InitialScreen") { inclusive = true }
                }
            }
        }
        false -> {
            LaunchedEffect(Unit) {
                navController.navigate("OnBoardingScreen") {
                    popUpTo("InitialScreen") { inclusive = true }
                }
            }
        }
    }
}
