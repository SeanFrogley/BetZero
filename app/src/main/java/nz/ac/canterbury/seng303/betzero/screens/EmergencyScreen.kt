package nz.ac.canterbury.seng303.betzero.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import nz.ac.canterbury.seng303.betzero.viewmodels.EmergencyViewModel
import nz.ac.canterbury.seng303.betzero.viewmodels.SlotShape
import org.koin.androidx.compose.koinViewModel

@Composable
fun EmergencyScreen(navController: NavController, viewModel: EmergencyViewModel = koinViewModel()) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AnimatedReel(viewModel.reel1, viewModel.isSpinning)
            AnimatedReel(viewModel.reel2, viewModel.isSpinning)
            AnimatedReel(viewModel.reel3, viewModel.isSpinning)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Disable the button while spinning
        Button(onClick = { viewModel.startSpinning() }, enabled = !viewModel.isSpinning) {
            Text(text = "Spin")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Balance: ${viewModel.balance}")

        Text(text = viewModel.outcomeMessage)
    }
}

@Composable
fun AnimatedReel(shape: SlotShape, isSpinning: Boolean) {
    var currentShape by remember { mutableStateOf(shape) }

    // Change the shape randomly every 200ms while spinning
    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            while (isSpinning) {
                currentShape = SlotShape.values().random() // Random shape
                delay(200) // Change the shape every 200ms
            }
        } else {
            currentShape = shape // Set the final shape after spinning
        }
    }

    // Draw the current shape
    Canvas(modifier = Modifier.size(100.dp)) {
        when (currentShape) {
            SlotShape.Circle -> drawCircle(color = Color.Red, radius = size.minDimension / 2)
            SlotShape.Rectangle -> drawRect(color = Color.Green, size = size)
            SlotShape.Triangle -> {
                val path = Path().apply {
                    moveTo(size.width / 2, 0f) // Top center
                    lineTo(size.width, size.height) // Bottom right
                    lineTo(0f, size.height) // Bottom left
                    close()
                }
                drawPath(path, color = Color.Blue)
            }
        }
    }
}
