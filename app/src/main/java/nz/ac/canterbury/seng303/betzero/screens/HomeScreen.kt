import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NewLabel
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.betzero.viewmodels.HomeViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = koinViewModel()) {

    val userProfile by viewModel.userProfile.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val showPopup = remember { mutableStateOf(true) }

    var userAge by rememberSaveable { mutableIntStateOf(0) }
    var userGoals by rememberSaveable { mutableStateOf<List<String>?>(null) }
    var newGoals: MutableList<String>? = userGoals?.toMutableList()
    var newGoal by rememberSaveable { mutableStateOf("") }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var goalInputError by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(userProfile) {
        userProfile?.let {
            userGoals = it.goals ?: emptyList()
            userAge = it.age
        }
    }
    val percentsThroughTimeList = viewModel.calculateTimePercentages(userAge)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            ) {
            Text(
                text = "Welcome ${userProfile?.name}",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                )

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = {
                    showPopup.value = true // Set to true to show the popup
                },
            ) {
                if (showPopup.value) {
                    Dialog(onDismissRequest = { showPopup.value = false }) {
                        PopupScreen(
                            onDismiss = {
                                showPopup.value = false
                            },
                            onSave = {}
                        )
                    }
                }
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = Icons.Default.NewLabel,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.primary
                )
            }


        }

        IntroMessage()

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 4.dp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(16.dp))

        // Iterate through stats and create progress bars for them
        Text(
            text = "You've made it this far, Keep going!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))


        for ((percent, period) in percentsThroughTimeList) {
            AgeIndicator(percent, period)
        }

        Spacer(Modifier.height(16.dp))



        Text(
            text = if (userGoals?.isEmpty() == true) "Create some Goals" else "My Goals",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(Modifier.height(10.dp))


        userGoals?.forEachIndexed { index, goal ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                IconButton(
                    onClick = {
                        val builder = android.app.AlertDialog.Builder(context)
                        builder.setMessage("Remove goal \"${goal}\"?")
                            .setCancelable(false)
                            .setPositiveButton("Remove") { dialog, id ->
                                newGoals = newGoals?.filterIndexed { i, _ -> i != index } as MutableList<String>?
                                viewModel.editGoals(userProfile!!, newGoals)
                                userGoals = newGoals

                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancel") { dialog, id ->
                                dialog.dismiss()
                            }
                        val alert = builder.create()
                        alert.show()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

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
                    Icon(Icons.Default.Add, contentDescription = "Add a new goal")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = Color.Transparent,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDialog = true }
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add a New Goal") },
            text = {
                OutlinedTextField(
                    value = newGoal,
                    onValueChange = { newGoal = it },
                    label = { Text("Goal") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newGoal.isNotBlank()) {
                            newGoals?.add(newGoal)
                             viewModel.editGoals(userProfile!!, newGoals)
                             userGoals = userGoals?.plus(newGoal)
                             newGoal = ""
                        } else {
                            goalInputError = viewModel.GOAL_INPUT_ERROR;
                        }
                        showDialog = false
                    }
                ) {
                    Text("Add Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun IntroMessage() {
    val introText = buildAnnotatedString {
        append("Welcome to BetZero, Set goals and use the navigation tab to navigate the app.\n")
        append("You can log your status everyday, these are accessible from the diary.\n" +
                "Use the calendar feature and analysis tab to track activity and trace spending.\n")
        append("Use the built in SOS slots feature to crave your gambling urges.\n")
        append("We wish you luck on your gambling free journey.")
    }

    Text(
        text = introText,
        fontSize = 18.sp,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        lineHeight = 24.sp
    )
}

//https://foso.github.io/Jetpack-Compose-Playground/material/circularprogressindicator/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeIndicator(progress: Float, period: String) {


    Column(horizontalAlignment = Alignment.Start) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.width(16.dp))

            LinearProgressIndicator(
                    modifier = Modifier
                        .height(10.dp)
                        .weight(1f),
            progress = { (progress) },
                    color = Color(0xFFFFA500),
                    trackColor = MaterialTheme.colorScheme.secondaryContainer,
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                    gapSize = (-8).dp //trying to remove default gap
                )


            Spacer(modifier = Modifier.width(16.dp))


            Text(
                    text = "${(period)} ${(progress * 100).toInt()}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}