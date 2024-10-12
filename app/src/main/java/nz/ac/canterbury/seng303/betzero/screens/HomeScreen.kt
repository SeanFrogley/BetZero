import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.betzero.viewmodels.HomeViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = koinViewModel()) {

    val userProfile by viewModel.userProfile.collectAsState()
    var newGoal by rememberSaveable { mutableStateOf("") }
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Home Screen")

    }

    HorizontalDivider(
        modifier = Modifier.fillMaxWidth()
    )

    Text(text = "My Goals", fontWeight = FontWeight.Bold, fontSize = 20.sp)

//    updatedGoals.forEachIndexed { index, goal ->
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            Text(
//                text = "$${String.format(Locale.US, "%s", goal)}",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 8.dp)
//            )
//        }
//    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = newGoal,
            onValueChange = { },
            readOnly = true,
            label = { Text("Add a new goal") },
            enabled = false,
            trailingIcon = {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Add a new goal")
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.White,
                disabledContainerColor = Color.Transparent,
                disabledBorderColor = Color.White,
                disabledLabelColor = Color.White,
                disabledTrailingIconColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true }
        )
    }

//    if (showDialog) {
//        AlertDialog(
//            onDismissRequest = { showDialog = false },
//            title = { Text("Add a New Goal") },
//            text = {
//                OutlinedTextField(
//                    value = newGoal,
//                    onValueChange = { newGoal = it },
//                    label = { Text("Goal") }
//                )
//            },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        if (newGoal.isNotBlank()) {
//                            updatedGoals.add(newGoal)
//                            if (userGoalId != null) {
//                                viewModel.editGoals(userGoalId, userGoals = UserGoals(userGoalId, updatedGoals.map { it }))
//                            }
//                            newGoal = ""
//                        }
//                        showDialog = false
//                    }
//                ) {
//                    Text("Add Goal")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDialog = false }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
}
