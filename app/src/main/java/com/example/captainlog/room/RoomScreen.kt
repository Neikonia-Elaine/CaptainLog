package com.example.captainlog.room

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RoomScreen(
    searchQuery: String,
    logTitles: List<String>,
    logTexts: Map<String, String>,
    expandedLogId: String?,
    isPlayingLogId: String?,
    friends: List<String>,
    sharedToFriends: Set<String>,
    onSearchQueryChange: (String) -> Unit,
    onToggleExpansion: (String) -> Unit,
    onTogglePlayback: (String) -> Unit,
    onShareLog: (String, String) -> Unit,
    onAddSharedFriend: (String) -> Unit,
    onClearSharedFriends: () -> Unit
) {
    var showShareDialog by remember { mutableStateOf(false) }
    var selectedLogForShare by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        Text(
            text = "Your Captain Quarters",
            color = Color(0xFF00FF9A),
            fontSize = 20.sp
        )

        Spacer(Modifier.height(16.dp))

        // Search Bar
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text("search in your logs...", color = Color(0xFF6CFF6C))
                },
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FF9A),
                    unfocusedBorderColor = Color(0xFF00FF9A),
                    focusedTextColor = Color(0xFF00FF9A),
                    unfocusedTextColor = Color(0xFF00FF9A),
                    cursorColor = Color(0xFF00FF9A)
                ),
                singleLine = true
            )

        }

        Spacer(Modifier.height(16.dp))

        // Log Cards List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val filteredLogs = if (searchQuery.isEmpty()) {
                logTitles
            } else {
                logTitles.filter { logTitle ->
                    logTitle.lowercase().contains(searchQuery.lowercase()) ||
                            logTexts[logTitle]?.lowercase()?.contains(searchQuery.lowercase()) == true
                }
            }

            items(filteredLogs) { logTitle ->
                LogCard(
                    logTitle = logTitle,
                    logText = logTexts[logTitle] ?: "",
                    isExpanded = expandedLogId == logTitle,
                    isPlaying = isPlayingLogId == logTitle,
                    onToggleExpansion = { onToggleExpansion(logTitle) },
                    onTogglePlayback = { onTogglePlayback(logTitle) },
                    onShare = {
                        selectedLogForShare = logTitle
                        showShareDialog = true
                    }
                )
            }
        }
    }

    // Share Dialog
    if (showShareDialog && selectedLogForShare != null) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .clickable {
                    showShareDialog = false
                    onClearSharedFriends()
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .fillMaxWidth(0.85f)
                    .border(2.dp, Color(0xFF00FF9A))
                    .background(Color.Black)
                    .padding(16.dp)
                    .clickable(enabled = false) { }
            ) {
                Text(
                    text = "SHARE LOG: $selectedLogForShare",
                    color = Color(0xFF00FF9A),
                    fontSize = 16.sp
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "SELECT FRIEND:",
                    color = Color(0xFFF9E97A),
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(12.dp))

                if (friends.isEmpty()) {
                    Text(
                        text = "No friends yet. Add friends in Social tab.",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        friends.forEach { friend ->
                            val isShared = sharedToFriends.contains(friend)
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFF8A8A8A))
                                    .background(Color(0xFF2A2A2A))
                                    .padding(12.dp)
                                    .clickable {
                                        if (!isShared) {
                                            onShareLog(selectedLogForShare!!, friend)
                                            onAddSharedFriend(friend)
                                        }
                                    },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = friend,
                                    color = Color(0xFF00FF9A),
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isShared) "✓" else "SHARE",
                                    color = if (isShared) Color(0xFF00FF9A) else Color(0xFFF9E97A),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .border(1.dp, Color(0xFF8A8A8A))
                        .background(Color.Black)
                        .clickable {
                            showShareDialog = false
                            onClearSharedFriends()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CLOSE",
                        color = Color(0xFFF9E97A),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun LogCard(
    logTitle: String,
    logText: String,
    isExpanded: Boolean,
    isPlaying: Boolean,
    onToggleExpansion: () -> Unit,
    onTogglePlayback: () -> Unit,
    onShare: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White)
            .background(Color.Black)
            .padding(12.dp)
    ) {
        // Header Row
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = logTitle,
                color = Color(0xFFF9E97A),
                fontSize = 16.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Share button
                Text(
                    text = "SHARE",
                    color = Color(0xFF00FF9A),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .border(1.dp, Color(0xFF00FF9A))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable { onShare() }
                )

                // Expand/Collapse arrow
                Text(
                    text = if (isExpanded) "▲" else "▼",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onToggleExpansion() }
                )
            }
        }

        // Expanded Content
        if (isExpanded) {
            Spacer(Modifier.height(12.dp))

            // Audio Waveform Display
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(1.dp, Color(0xFF00FF9A))
                    .background(Color.Black)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // Fake waveform bars
                repeat(50) {
                    val height = (10..100).random() / 100f
                    Box(
                        Modifier
                            .width(2.dp)
                            .fillMaxHeight(height)
                            .background(Color(0xFF00FF9A))
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Play Button
            Text(
                text = if (isPlaying) "|| PAUSE" else "▶ PLAY",
                color = Color(0xFFF9E97A),
                fontSize = 14.sp,
                modifier = Modifier
                    .border(1.dp, Color(0xFF8A8A8A))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clickable { onTogglePlayback() }
            )

            Spacer(Modifier.height(12.dp))

            // Transcribed Text
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF4E4B4B))
                    .padding(10.dp)
            ) {
                Text(
                    text = logText,
                    color = Color(0xFFF9E97A),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}