package nz.ac.canterbury.seng303.betzero.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import nz.ac.canterbury.seng303.betzero.viewmodels.EmergencyViewModel
import nz.ac.canterbury.seng303.betzero.viewmodels.SlotShape
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Surface
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Icon
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

@Composable
fun EmergencyScreen(navController: NavController, viewModel: EmergencyViewModel = koinViewModel()) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (viewModel.balance >= 5) {
                    showDialog = true
                } else {
                    Toast.makeText(context, "You are out of coins!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .size(80.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AttachMoney,
                contentDescription = "Open Slot Machine",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        if (showDialog) {
            SlotMachinePopup(
                onClose = { showDialog = false },
                viewModel = viewModel
            )
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
            color = Color.White,
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
