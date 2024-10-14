package nz.ac.canterbury.seng303.betzero.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.random.Random

/**
 * The PopupScreen displays a dialog where users can select their mood, record a voice memo,
 * and save the information selected as their daily entry. It also includes a random motivational quote from
 * the list in this file.
 *
 * Parameters:
 * - onDismiss: A lambda function to be called when the dialog is dismissed.
 * - onSave: A lambda function to be called when the daily log entry is saved.
 *
 * State Variables:
 * - hasRecordingPermission: Boolean state to track if the app has permission to record audio.
 * - isRecording: Boolean state to track if the app is currently recording audio.
 * - selectedMood: String state to track the selected mood.
 * - voiceMemoPath: String state to track the path of the recorded voice memo.
 * - showExitConfirmation: Boolean state to track if the exit confirmation dialog should be shown.
 * - recordButtonTapCount: Int state to track the number of times the record button has been tapped.
 * - isMoodSelected: Boolean state to track if a mood has been selected.
 *
 * Other Variables:
 * - context: The current context.
 * - quotes: A list of motivational quotes.
 * - randomQuote: A randomly selected quote from the quotes list.
 * - voiceRecorder: An instance of the VoiceRecorder class.
 * - requestPermissionLauncher: A launcher to request audio recording permission.
 *
 * @author Michelle Lee
 */
@Composable
fun PopupScreen(
    onDismiss: () -> Unit,
    onSave: (DailyLog) -> Unit
) {
    val context = LocalContext.current
    val quotes = listOf(
        "The safest way to double your money is to fold it over and put it in your pocket.",
        "Gambling: The sure way of getting nothing for something.",
        "The house always wins. Remember that when you're tempted to play",
        "Gambling is a way of playing for a better future that often leads to a worse present.",
        "Each time you gamble, you gamble your future for a fleeting moment of excitement.",
        "Don't let gambling steal your joy; true happiness can't be bought."
    )
    val randomQuote = quotes[Random.nextInt(quotes.size)]
    var hasRecordingPermission by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    val voiceRecorder = remember { VoiceRecorder(context) }
    var selectedMood by remember { mutableStateOf("") }
    var voiceMemoPath by remember { mutableStateOf<String?>(null) }
    var showExitConfirmation by remember { mutableStateOf(false) }
    var recordButtonTapCount by remember { mutableStateOf(0) }
    var isMoodSelected by remember { mutableStateOf(false) } // To disable moodIcons once one is selected

    // Request permission for recording
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasRecordingPermission = isGranted
    }

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
            onDismissRequest = {
                showExitConfirmation = false
                               },
            title = {
                Text("Exit Confirmation")
                    },
            text = {
                Text("You cannot come back and record. Are you sure you want to exit?")
                   },
            confirmButton = {
                Button(
                    onClick = {
                        showExitConfirmation = false
                        onDismiss()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                    showExitConfirmation = false
                })
                {
                    Text(
                        "No"
                    )
                }
            }
        )
    }

    // Main content of the popup dialog
    AlertDialog(
        onDismissRequest = {
            showExitConfirmation = true
                           },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Welcome Back!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = {
                        showExitConfirmation = true
                              },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "How are you feeling today?",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = {
                            selectedMood = "Sad"
                            isMoodSelected = true
                                  },
                        enabled = !isMoodSelected,
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
                        onClick = {
                            selectedMood = "Neutral"
                            isMoodSelected = true
                                  },
                        enabled = !isMoodSelected,
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
                        onClick = {
                            selectedMood = "Happy"
                            isMoodSelected = true
                                  },
                        enabled = !isMoodSelected,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SentimentVerySatisfied,
                            contentDescription = "Happy",
                            modifier = Modifier.size(48.dp),
                            tint = if (selectedMood == "Happy") Color.Yellow else Color.Gray // This line changes the tint color
                        )
                    }
                }
                // Dividers!
                Spacer(
                    modifier = Modifier.height(16.dp)
                )
                Divider()
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                if (hasRecordingPermission) {
                    Button(
                        onClick = {
                            if (!selectedMood.isNullOrEmpty()) {
                                if (isRecording) {
                                    voiceMemoPath = voiceRecorder.stopRecording()
                                } else {
                                    voiceRecorder.startRecording(selectedMood) // Pass mood for file name
                                }
                                isRecording = !isRecording
                                recordButtonTapCount++ // Increment tap count
                            } else {
                                Toast.makeText(context, "Please select a mood before recording.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = recordButtonTapCount < 2, // Disable button after 2 taps
                        modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = if (isRecording) "Stop Recording" else "Record",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                } else {
                    Text(
                        "Recording permission is required.",
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Spacer(
                    modifier = Modifier.height(16.dp)
                )
                Divider()
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
                Text(
                    "Daily Quote:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    randomQuote,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (voiceMemoPath != null) {
                        val currentDateTime = LocalDateTime.now()
                        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val formattedDate = currentDateTime.format(dateFormat)
                        val entry = DailyLog(
                            id = Random.nextInt(),
                            feeling = selectedMood,
                            voiceMemo = voiceMemoPath!!,
                            date = formattedDate,
                            completed = true // Mark log as complete
                        )
                        onSave(entry)
                        onDismiss()
                        Toast.makeText(context, "Log saved successfully.", Toast.LENGTH_SHORT).show() // Show success message
                    } else {
                        Toast.makeText(context, "Please record a voice memo before saving.", Toast.LENGTH_SHORT).show()
                    }
                },
            ) {
                Text("Save")
            }
        }
    )
}