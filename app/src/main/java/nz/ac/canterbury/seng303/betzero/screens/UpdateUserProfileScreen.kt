package nz.ac.canterbury.seng303.betzero.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.betzero.viewmodels.UpdateUserProfileViewModel
import nz.ac.canterbury.seng303.betzero.viewmodels.UserProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun UpdateUserProfileScreen(navController: NavController, viewModel: UpdateUserProfileViewModel = koinViewModel()) {
    val userProfile by viewModel.userProfile.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Edit profile Screen")
    }
}