package nz.ac.canterbury.seng303.betzero.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import android.widget.ToggleButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.betzero.utils.UserUtil
import nz.ac.canterbury.seng303.betzero.viewmodels.PreferencesViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                    onClick = { onOptionSelected(index) },
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
