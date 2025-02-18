package com.example.mobilecomputing.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.geometry.Offset
import com.example.mobilecomputing.SensorBackgroundService
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun SensorScreen(onNavigateBack: () -> Unit) {
    val sensorValues by remember {
        SensorBackgroundService.sensorData
    }.observeAsState(SensorBackgroundService.SensorValues())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Accelerometer Sensor Data",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Accelerometer readings
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Current Readings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("X: ${String.format("%.2f", sensorValues.x)} m/s²")
                    Text("Y: ${String.format("%.2f", sensorValues.y)} m/s²")
                    Text("Z: ${String.format("%.2f", sensorValues.z)} m/s²")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Movement Magnitude: ${String.format("%.2f", sensorValues.magnitude)} m/s²",
                    fontWeight = FontWeight.Bold
                )

                // Visual representation of movement
                Spacer(modifier = Modifier.height(16.dp))

                MovementVisualizer(
                    x = sensorValues.x,
                    y = sensorValues.y,
                    z = sensorValues.z,
                    magnitude = sensorValues.magnitude
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Threshold information
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Notification Threshold",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Current threshold: ${SensorBackgroundService.THRESHOLD} m/s²"
                )

                Text(
                    text = if (sensorValues.magnitude > SensorBackgroundService.THRESHOLD)
                        "Threshold exceeded! Notification sent."
                    else
                        "Below threshold - no notification.",
                    color = if (sensorValues.magnitude > SensorBackgroundService.THRESHOLD)
                        Color.Red
                    else
                        Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNavigateBack,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Text("Back")
        }
    }
}

@Composable
fun MovementVisualizer(x: Float, y: Float, z: Float, magnitude: Float) {
    val threshold = SensorBackgroundService.THRESHOLD

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .border(1.dp, Color.LightGray)
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f

            // Draw background grid lines
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, centerY),
                end = Offset(width, centerY),
                strokeWidth = 1f
            )

            drawLine(
                color = Color.LightGray,
                start = Offset(centerX, 0f),
                end = Offset(centerX, height),
                strokeWidth = 1f
            )

            // Scale factor to visualize movement
            val scaleFactor = width / 4f / 20f  // Scale to fit in a quarter of width for 20 m/s²

            // Draw threshold circle
            drawCircle(
                color = Color.Red.copy(alpha = 0.3f),
                radius = threshold * scaleFactor,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2f)
            )

            // Draw point for current position
            val xOffset = x * scaleFactor
            val yOffset = y * scaleFactor

            drawCircle(
                color = Color.Blue,
                radius = 10f,
                center = Offset(centerX + xOffset, centerY - yOffset)
            )

            // Draw line for Z axis (adjust for visibility)
            val zLength = z * scaleFactor / 2
            drawLine(
                color = Color.Green,
                start = Offset(centerX + xOffset, centerY - yOffset),
                end = Offset(centerX + xOffset, centerY - yOffset - zLength),
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
        }

        // Labels
        Text(
            text = "X",
            color = Color.Blue,
            modifier = Modifier.align(Alignment.CenterEnd)
        )

        Text(
            text = "Y",
            color = Color.Blue,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        Text(
            text = "Z",
            color = Color.Green,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview
@Composable
fun SensorScreenPreview() {
    SensorScreen(onNavigateBack = {})
}