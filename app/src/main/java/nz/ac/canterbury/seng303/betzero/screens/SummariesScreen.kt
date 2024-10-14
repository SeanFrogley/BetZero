package nz.ac.canterbury.seng303.betzero.screens

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.betzero.models.DailyLog
import nz.ac.canterbury.seng303.betzero.utils.RecordingUtil
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter.ofPattern
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * The SummariesScreen displays a list of daily log entries which shows details such as:
 * - date, timestamp
 * - mood icon
 * - and a play button allowing users to play or pause their recording.
 *
 * State Variables:
 * - recordings: List<DailyLog> state to track the list of daily log entries.
 * - mediaPlayer: MediaPlayer state to manage the media player instance.
 * - currentlyPlayingId: Int state to track the ID of the currently playing log entry.
 *
 * @author Michelle Lee
 */

@Composable
fun SummariesScreen() {
    val context = LocalContext.current
    var recordings by remember { mutableStateOf(emptyList<DailyLog>()) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentlyPlayingId by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            recordings = RecordingUtil.getAllRecordings(context)
                .sortedByDescending { it.lastModified() }
                .mapNotNull { file ->
                    val feeling = RecordingUtil.getMoodFromFile(file)
                    if (feeling != null) {
                        DailyLog(
                            id = file.hashCode(),
                            feeling = feeling,
                            voiceMemo = file.name,
                            // Updated date formatting to include timestamp
                            date = ofPattern("yyyy-MM-dd, H:mm:ss", Locale.getDefault())
                                .format(LocalDateTime.ofEpochSecond(file.lastModified() / 1000, 0, ZoneOffset.UTC))
                        )
                    } else {
                        null // Skip entries without a valid mood
                    }
                }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (recordings.isEmpty()) {
            // Display a message when there are no recordings
            Text(
                text = "No summaries available, please log one!",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn {
                items(recordings) { entry ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.LightGray)
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(text = entry.date)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val moodIcon = when (entry.feeling) {
                                    "Happy" -> Icons.Default.SentimentVerySatisfied
                                    "Neutral" -> Icons.Default.SentimentNeutral
                                    "Sad" -> Icons.Default.SentimentVeryDissatisfied
                                    else -> Icons.Default.QuestionMark
                                }
                                Icon(
                                    imageVector = moodIcon,
                                    contentDescription = entry.feeling
                                )
                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )
                                Button(
                                    onClick = {
                                        if (currentlyPlayingId == entry.id) {
                                            mediaPlayer?.pause()
                                            currentlyPlayingId = null
                                        } else {
                                            mediaPlayer?.release()
                                            mediaPlayer = MediaPlayer().apply {
                                                setDataSource(RecordingUtil.getRecordingFile(context, entry.voiceMemo).absolutePath)
                                                prepare()
                                                start()
                                            }
                                            currentlyPlayingId = entry.id
                                        }
                                    },
                                ) {
                                    Text(if (currentlyPlayingId == entry.id) "Pause" else "Play")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
