import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.betzero.models.DailyLog
import nz.ac.canterbury.seng303.betzero.utils.RecordingUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SummariesScreen(navController: NavController) {
    val context = LocalContext.current
    var recordings by remember { mutableStateOf(emptyList<DailyLog>()) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentlyPlayingId by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            recordings = RecordingUtil.getAllRecordings(context).map { file ->
                DailyLog(
                    id = file.hashCode(),
                    feeling = file.name, // Replace with actual mood if available
                    voiceMemo = file.name,
                    date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(file.lastModified()))
                )
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
                                else -> Icons.Default.SentimentNeutral
                            }
                            Icon(imageVector = moodIcon, contentDescription = entry.feeling)
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
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
                            }) {
                                Text(if (currentlyPlayingId == entry.id) "Pause" else "Play")
                            }
                        }
                    }
                }
            }
        }
    }
}