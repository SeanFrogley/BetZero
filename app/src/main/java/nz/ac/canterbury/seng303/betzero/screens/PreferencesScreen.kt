package nz.ac.canterbury.seng303.betzero.screens

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.betzero.viewmodels.PreferencesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PreferencesScreen(navController: NavController, viewModel: PreferencesViewModel = koinViewModel()) {
    val userProfile by viewModel.userProfile.collectAsState()

    var isDarkMode by rememberSaveable { mutableStateOf(false) }
    var isUserEnforcedTheme by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(userProfile) {
        userProfile?.let {
            isDarkMode = it.isDarkMode
            isUserEnforcedTheme = it.isUserEnforcedTheme
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var selectedOption by remember { mutableStateOf(0) }

        if (isUserEnforcedTheme) {
            selectedOption = if (isDarkMode) 2 else 0
        } else {
            selectedOption = 1
        }

        Text(text = "Preferences", fontWeight = FontWeight.Bold, fontSize = 20.sp)


        Text(text = "Application Colour Theme", fontSize = 16.sp)

        ThreePositionSwitch(
            selectedOption = selectedOption,
            onOptionSelected = { newIndex ->
                selectedOption = newIndex
                viewModel.updateThemeSettings(newIndex)
            }
        )



    }
}


@Composable
fun ThreePositionSwitch(
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit
) {
    val options = listOf("Light", "System", "Dark")
    val context = LocalContext.current


    // Box to contain the rounded background
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer, // Secondary color for the background
                shape = RoundedCornerShape(26.dp) // Rounded corners
            )
            .fillMaxWidth() // Take up the full width
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            options.forEachIndexed { index, option ->
                Button(
                    onClick = {
                        onOptionSelected(index)
                        Toast.makeText(context, "Theme applied! App restart required!", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedOption == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = option,
                        color = if (selectedOption == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
