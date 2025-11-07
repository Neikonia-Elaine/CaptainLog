package com.example.captainlog.log

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LogScreen(
    username: String,
    isRecording: Boolean,
    audioAvailable: Boolean,
    amplitude: Int,
    amplitudeHistory: List<Int>,
    transcribedText: String,
    isPlaying: Boolean,
    saveStatus: String,
    haveMicPermission: Boolean,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onTranscribe: () -> Unit,
    onPlayback: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
            .padding(WindowInsets.statusBars.asPaddingValues())
    ) {

        Text(
            text = "Welcome, Captain $username",
            color = Color(0xFF00FF9A),
            fontSize = 18.sp
        )

        Spacer(Modifier.height(16.dp))

        // Control Buttons Row
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RecordButton(
                label = if (isRecording) "STOP" else "RECORD",
                enabled = haveMicPermission || !isRecording,
                onClick = {
                    if (isRecording) {
                        onStopRecording()
                    } else {
                        onStartRecording()
                    }
                }
            )

            RecordButton(
                label = "TRANSCRIBE",
                enabled = audioAvailable && !isRecording,
                onClick = onTranscribe
            )

            RecordButton(
                label = if (isPlaying) "STOP" else "PLAYBACK",
                enabled = audioAvailable && !isRecording,
                onClick = onPlayback
            )

            RecordButton(
                label = "SAVE",
                enabled = audioAvailable && !isRecording,
                onClick = onSave
            )
        }

        Spacer(Modifier.height(20.dp))

        // Waveform Display Area
        Box(
            Modifier
                .fillMaxWidth()
                .height(40.dp)
                .border(2.dp, Color(0xFF00FF9A))
                .background(Color.Black)
                .padding(4.dp)
        ) {
            Row(
                Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                if (amplitudeHistory.isNotEmpty()) {
                    val maxBars = 100
                    val displayData = if (amplitudeHistory.size > maxBars) {
                        amplitudeHistory.takeLast(maxBars)
                    } else {
                        amplitudeHistory
                    }

                    displayData.forEach { amp ->
                        val normalizedHeight = (amp / 32767f).coerceIn(0.05f, 1f)
                        Box(
                            Modifier
                                .width(3.dp)
                                .fillMaxHeight(normalizedHeight)
                                .background(Color(0xFF00FF9A))
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Recording Indicator
        if (isRecording) {
            Box(
                Modifier
                    .fillMaxWidth((amplitude / 32767f).coerceIn(0f, 1f))
                    .height(12.dp)
                    .background(Color(0xFF00FF9A))
            )
            Spacer(Modifier.height(16.dp))
            Text("Recording...", color = Color(0xFF00FF9A))
        }

        Spacer(Modifier.height(20.dp))

        // Save Status Display
        if (saveStatus.isNotEmpty()) {
            Text(
                saveStatus,
                color = if (saveStatus.startsWith("ERROR")) Color.Red else Color(0xFF00FF9A),
                fontSize = 14.sp
            )
            Spacer(Modifier.height(12.dp))
        }

        // Transcription Display Area
        Column(
            Modifier
                .fillMaxWidth()
                .height(360.dp)
                .background(Color(0xFF4E4B4B))
                .padding(10.dp)
//                .border(1.dp, Color(0xFF888888))
        ) {
            if (transcribedText.isNotEmpty()) {
                Text(
                    transcribedText,
                    color = Color(0xFFF9E97A),
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
            } else {
                Text("Awaiting log entry...", color = Color.Gray)
            }
        }
    }
}

@Composable
private fun RecordButton(
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val borderColor = if (enabled) Color(0xFF8A8A8A) else Color(0xFF4A4A4A)
    val textColor = if (enabled) Color(0xFFF9E97A) else Color(0xFF6C6C6C)

    Box(
        modifier = Modifier
            .width(85.dp)
            .height(42.dp)
            .border(1.dp, borderColor)
            .background(Color.Black)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor
        )
    }
}