package nz.ac.canterbury.seng303.betzero.screens

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Textsms
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import nz.ac.canterbury.seng303.betzero.models.DailyLog
import nz.ac.canterbury.seng303.betzero.models.SlotShape
import nz.ac.canterbury.seng303.betzero.utils.RecordingUtil
import nz.ac.canterbury.seng303.betzero.viewmodels.EmergencyViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EmergencyScreen(viewModel: EmergencyViewModel = koinViewModel()) {
    val context = LocalContext.current
    var showArticlesDialog by rememberSaveable { mutableStateOf(false) }
    var showSlotMachineDialog by rememberSaveable { mutableStateOf(false) }
    var happyRecordings by remember { mutableStateOf(emptyList<DailyLog>()) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentlyPlayingId by remember { mutableStateOf<Int?>(null) }


    LaunchedEffect(Unit) {
        happyRecordings = RecordingUtil.getAllRecordings(context)
            .filter { RecordingUtil.getMoodFromFile(it) == "Happy" } // Filter for happy recordings
            .sortedByDescending { it.lastModified() }
            .mapNotNull { file ->
                DailyLog(
                    id = file.hashCode(),
                    feeling = "Happy",
                    voiceMemo = file.name,
                    date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(file.lastModified()))
                )
            }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "So you're feeling down...?",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 15.dp),
            fontSize = 30.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = Color.Red
        )
        Text(
            text = "Let us cheer you up! ",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Text(
            text = "Remember this time when you were doing amazing?!\n Let's listen to some happy recordings and celebrate your success!",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontSize = 13.sp,
        )

        IconButton(
            onClick = {
                // Logic to play a random recording with the mood "Happy"
                if (happyRecordings.isNotEmpty()) {
                    val randomRecording = happyRecordings.random()
                    if (currentlyPlayingId == randomRecording.id) {
                        mediaPlayer?.pause()
                        currentlyPlayingId = null
                    } else {
                        mediaPlayer?.release()
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(RecordingUtil.getRecordingFile(context, randomRecording.voiceMemo).absolutePath)
                            prepare()
                            start()
                        }
                        currentlyPlayingId = randomRecording.id
                    }
                } else {
                    Toast.makeText(context, "No happy recordings found", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .size(100.dp)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayCircleOutline,
                contentDescription = "Play",
                modifier = Modifier.size(120.dp),
                tint = Color.Yellow
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Divider()
        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "If that's not enough... don't be ashamed! \nWe have some other ways to cheer you up!",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Have a go at a non-risk gamble with the slot machine\n or read some inspiring articles to keep you going!",
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (viewModel.balance >= 5) {
                        showSlotMachineDialog = true
                    } else {
                        Toast.makeText(context, "You are out of coins!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = "Open Slots",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Button(
                onClick = {
                    showArticlesDialog = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Articles Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )
        Divider()
        Spacer(
            modifier = Modifier.height(50.dp)
        )

        Text(
            text = "Want to talk to someone?",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = Color.Red
        )
        Text(
            text = "Reach out to Gambling Helpline Aoteroa available 24/7\n See contact details below",
            fontSize = 12.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Row {
            Icon(
                imageVector = Icons.Default.Phone ,
                contentDescription = "Phone Icon",
                Modifier.padding(start = 8.dp)
                )
            Text(text ="0800 654 655", modifier = Modifier.padding(start = 8.dp))
        }

        Row {
            Icon(
                imageVector = Icons.Default.Textsms ,
                contentDescription = "Phone Icon",
                Modifier.padding(start = 8.dp)
            )
            Text(text ="8006", modifier = Modifier.padding(start = 8.dp))
        }


        if (showSlotMachineDialog) {
            SlotMachinePopup(
                onClose = { showSlotMachineDialog = false },
                viewModel = viewModel
            )
        }

        if (showArticlesDialog) {
            ArticlesPopup(onClose = { showArticlesDialog = false })
        }
    }
}

@Composable
fun SlotMachinePopup(onClose: () -> Unit, viewModel: EmergencyViewModel) {
    Dialog(
        onDismissRequest = { onClose() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(0.9f)
                .padding(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Balance: ${viewModel.balance}",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp),
                    fontWeight = FontWeight.Bold
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AnimatedReel(viewModel.reel1, viewModel.isSpinning)
                        Spacer(modifier = Modifier.width(16.dp))
                        AnimatedReel(viewModel.reel2, viewModel.isSpinning)
                        Spacer(modifier = Modifier.width(16.dp))
                        AnimatedReel(viewModel.reel3, viewModel.isSpinning)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val outcomeColor = if (viewModel.outcomeMessage == "You win!") Color.Green else Color.Red
                    Text(
                        text = viewModel.outcomeMessage,
                        color = outcomeColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { onClose() },
                        modifier = Modifier.width(120.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Close")
                    }

                    Button(
                        onClick = { viewModel.startSpinning() },
                        enabled = !viewModel.isSpinning,
                        modifier = Modifier.width(120.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Spin")
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedReel(shape: SlotShape, isSpinning: Boolean) {
    var currentShape by remember { mutableStateOf(shape) }

    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            while (isSpinning) {
                currentShape = SlotShape.entries.toTypedArray().random()
                delay(200)
            }
        } else {
            currentShape = shape
        }
    }

    Canvas(modifier = Modifier.size(60.dp)) {
        when (currentShape) {
            SlotShape.Circle -> drawCircle(color = Color.Red, radius = size.minDimension / 2)
            SlotShape.Rectangle -> drawRect(color = Color.Green, size = size)
            SlotShape.Triangle -> {
                val path = Path().apply {
                    moveTo(size.width / 2, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(path, color = Color.Blue)
            }
        }
    }
}

@Composable
fun ArticlesPopup(onClose: () -> Unit) {
    val context = LocalContext.current  // To open links
    val articles = listOf(
        Article(
            title = "Be inspired to quit: Success stories from ex-smokers in the region",
            url = "https://www.who.int/westernpacific/news-room/feature-stories/item/be-inspired-to-quit-success-stories-from-ex-smokers-in-the-region"
        ),
        Article(
            title = "Five action steps for quitting an addiction",
            url = "https://www.health.harvard.edu/diseases-and-conditions/five-action-steps-for-quitting-an-addiction"
        ),
        Article(
            title = "I need a strong motivation to quit gambling",
            url = "https://www.quora.com/I-need-a-strong-motivation-to-quit-gambling-About-2-years-ago-I-was-introduced-to-sport-betting-by-my-brother-and-the-thing-is-killing-me-secretly-and-its-hard-for-me-to-quit-What-can-I-do"
        ),
        Article(
            title = "How to stop gambling",
            url = "https://www.gatewayfoundation.org/blog/how-to-stop-gambling/"
        ),
        Article(
            title = "Road to recovery from gambling addiction",
            url = "https://www.alustforlife.com/voice/road-to-recovery-from-gambling-addiction"
        ),
        Article(
            title = "Gambling: How to change your habits",
            url = "https://www.betterhealth.vic.gov.au/health/healthyliving/gambling-how-to-change-your-habits"
        )
    )

    Dialog(
        onDismissRequest = { onClose() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(0.9f)
                .padding(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Inspiring Articles",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    articles.chunked(2).forEach { rowArticles ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowArticles.forEach { article ->
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp)
                                        .padding(8.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = article.title,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        ScrollingText(
                                            text = article.title,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onClose() },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
fun ScrollingText(text: String) {
    var textWidth by remember { mutableStateOf(0f) }
    var boxWidth by remember { mutableStateOf(0f) }

    val isScrollable = textWidth > boxWidth

    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(isScrollable, textWidth, boxWidth) {
        offsetX.stop()
        if (isScrollable) {
            val fullScrollWidth = textWidth - boxWidth
            offsetX.updateBounds(-fullScrollWidth, 0f)
            offsetX.animateTo(
                targetValue = -fullScrollWidth,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 6000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            offsetX.snapTo(0f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clipToBounds()
            .onGloballyPositioned { layoutCoordinates ->
                boxWidth = layoutCoordinates.size.width.toFloat()
            }
    ) {
        Text(
            text = text,
            maxLines = 1,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier
                .offset { IntOffset(x = offsetX.value.toInt(), y = 0) }
                .wrapContentWidth(unbounded = true)
                .onGloballyPositioned { layoutCoordinates ->
                    textWidth = layoutCoordinates.size.width.toFloat()
                }
        )
    }
}



data class Article(
    val title: String,
    val url: String
)
