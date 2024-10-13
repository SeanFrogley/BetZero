package nz.ac.canterbury.seng303.betzero.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import nz.ac.canterbury.seng303.betzero.models.DailyLog
import nz.ac.canterbury.seng303.betzero.utils.VoiceRecorder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Composable
fun PopupScreen(onDismiss: () -> Unit, onSave: (DailyLog) -> Unit) {
    val context = LocalContext.current
    val quotes = listOf("Quote 1", "Quote 2", "Quote 3")
    val randomQuote = quotes[Random.nextInt(quotes.size)]
    var hasRecordingPermission by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    val voiceRecorder = remember { VoiceRecorder(context) }
    var selectedMood by remember { mutableStateOf("Neutral") }
    var voiceMemoPath by remember { mutableStateOf<String?>(null) }
    var showExitConfirmation by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Request permission for recording
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean -> hasRecordingPermission = isGranted }

    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) -> {
                hasRecordingPermission = true
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
    // If the user wants to exit the pop-up dialog, display warning
    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = { showExitConfirmation = false },
            title = { Text("Exit Confirmation") },
            text = { Text("You cannot come back and record. Are you sure you want to exit?") },
            confirmButton = {
                Button(onClick = {
                    showExitConfirmation = false
                    onDismiss()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showExitConfirmation = false }) {
                    Text("No")
                }
            }
        )
    }

    // Main content of the popup dialog
    AlertDialog(
        onDismissRequest = { showExitConfirmation = true },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Welcome Back!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = { showExitConfirmation = true }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Black
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("How are you feeling today?", fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = { selectedMood = "Sad" },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SentimentVeryDissatisfied,
                            contentDescription = "Sad",
                            modifier = Modifier.size(48.dp),
                            tint = if (selectedMood == "Sad") Color.Blue else Color.Gray
                        )
                    }

                    IconButton(
                        onClick = { selectedMood = "Neutral" },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SentimentNeutral,
                            contentDescription = "Neutral",
                            modifier = Modifier.size(48.dp),
                            tint = if (selectedMood == "Neutral") Color.Green else Color.Gray
                        )
                    }

                    IconButton(
                        onClick = { selectedMood = "Happy" }, modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SentimentVerySatisfied,
                            contentDescription = "Happy",
                            modifier = Modifier.size(48.dp),
                            tint = if (selectedMood == "Happy") Color.Yellow else Color.Gray
                        )
                    }
                }
                // Dividers!
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                if (hasRecordingPermission) {
                    Button(
                        onClick = {
                            if (selectedMood != "Neutral") {
                                if (isRecording) {
                                    voiceMemoPath = voiceRecorder.stopRecording()
                                } else {
                                    voiceRecorder.startRecording(selectedMood) // Pass mood for file name
                                }
                                isRecording = !isRecording
                                errorMessage = null // Clear error if action is successful
                            } else {
                                errorMessage = "Please select a mood before recording."
                            }
                        },
                        enabled = selectedMood != "Neutral", // Disable if the mood is not selected
                        modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = if (isRecording) "Stop Recording" else "Record",
                            modifier = Modifier.size(64.dp),
                            tint = if (selectedMood == "Neutral") Color.Gray else Color.Unspecified // Change icon color based on mood
                        )
                    }
                } else {
                    Text("Recording permission is required.", fontSize = 16.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                // Displaying error message if any
                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = Color.Red,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Daily Quote:", fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(8.dp))
                Text(randomQuote, fontSize = 16.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (voiceMemoPath != null) {
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val formattedDate = dateFormat.format(Date())
                        val entry = DailyLog(
                            id = Random.nextInt(),
                            feeling = selectedMood,
                            voiceMemo = voiceMemoPath!!,
                            date = formattedDate,
                            completed = true // Mark log as complete
                        )

                        onSave(entry)
                        onDismiss()
                    }
                },
                enabled = voiceMemoPath != null
            ) {
                Text("Save")
            }
        }
    )
}
