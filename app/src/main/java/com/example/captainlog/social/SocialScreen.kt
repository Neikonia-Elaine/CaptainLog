package com.example.captainlog.social

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
fun SocialScreen(
    searchQuery: String,
    searchResult: String,
    friends: List<String>,
    sharedLogs: Map<String, List<String>>,
    receivedLogs: Map<String, Map<String, String>>,
    expandedReceivedLog: Pair<String, String>?,
    statusMessage: String,
    loading: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchUser: () -> Unit,
    onAddFriend: (String) -> Unit,
    onRemoveFriend: (String) -> Unit,
    onClearSearchResult: () -> Unit,
    onToggleReceivedLog: (String, String) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        Text(
            text = "Finding Other Starships...",
            color = Color(0xFF00FF9A),
            fontSize = 20.sp
        )

        Spacer(Modifier.height(16.dp))

        // Search User Section
        Column(
            Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF00FF9A))
                .padding(12.dp)
        ) {
            Text(
                text = "SEARCH CAPTAIN",
                color = Color(0xFFF9E97A),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text("enter captain ID...", color = Color(0xFF6CFF6C))
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
                    singleLine = true,
                )

                Box(
                    modifier = Modifier
                        .height(55.dp)
                        .border(1.dp, Color(0xFF8A8A8A))
                        .background(Color.Black)
                        .clickable(enabled = !loading && searchQuery.isNotBlank()) {
                            onSearchUser()
                        }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (loading) "..." else "SEARCH",
                        color = if (searchQuery.isNotBlank() && !loading)
                            Color(0xFFF9E97A) else Color(0xFF6C6C6C),
                        fontSize = 16.sp
                    )
                }
            }

            // Search Result Display
            if (searchResult.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2A2A2A))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = searchResult,
                        color = Color(0xFF00FF9A),
                        fontSize = 14.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "ADD",
                            color = Color(0xFFF9E97A),
                            fontSize = 12.sp,
                            modifier = Modifier
                                .border(1.dp, Color(0xFF00FF9A))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .clickable { onAddFriend(searchResult) }
                        )

                        Text(
                            text = "✕",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onClearSearchResult() }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Status Message
        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                color = if (statusMessage.startsWith("ERROR"))
                    Color.Red else Color(0xFF00FF9A),
                fontSize = 14.sp
            )
            Spacer(Modifier.height(12.dp))
        }



        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "FRIEND LIST (${friends.size})",
                    color = Color(0xFFF9E97A),
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(12.dp))
            }

            items(friends) { friend ->
                FriendCard(
                    friendName = friend,
                    sharedLogCount = sharedLogs[friend]?.size ?: 0,
                    onRemove = { onRemoveFriend(friend) }
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "RECEIVED LOGS",
                    color = Color(0xFFF9E97A),
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(12.dp))
            }
            // Received logs from friends
            receivedLogs.forEach { (friendName, logs) ->
                items(logs.entries.toList()) { (logTitle, logText) ->
                    ReceivedLogCard(
                        friendName = friendName,
                        logTitle = logTitle,
                        logText = logText,
                        isExpanded = expandedReceivedLog == (friendName to logTitle),
                        onToggleExpand = {
                            onToggleReceivedLog(friendName, logTitle)
                        }
                    )
                }
            }


        }
    }
}

@Composable
private fun FriendCard(
    friendName: String,
    sharedLogCount: Int,
    onRemove: () -> Unit
) {
    var showLogs by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White)
            .background(Color.Black)
            .padding(12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friendName,
                    color = Color(0xFFF9E97A),
                    fontSize = 15.sp
                )
                Text(
                    text = "Shared logs: $sharedLogCount",
                    color = Color(0xFF6CFF6C),
                    fontSize = 12.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "REMOVE",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .border(1.dp, Color.Red)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable { onRemove() }
                )

                if (sharedLogCount > 0) {
                    Text(
                        text = if (showLogs) "▲" else "▼",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { showLogs = !showLogs }
                    )
                }
            }
        }

        // Show shared logs when expanded
        if (showLogs && sharedLogCount > 0) {
            Spacer(Modifier.height(8.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2A2A2A))
                    .padding(8.dp)
            ) {
                Text(
                    text = "SHARED LOGS:",
                    color = Color(0xFF00FF9A),
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(4.dp))
                // Placeholder for shared log titles
                Text("Log 1", color = Color(0xFFF9E97A), fontSize = 12.sp)
                Text("Log 2", color = Color(0xFFF9E97A), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ReceivedLogCard(
    friendName: String,
    logTitle: String,
    logText: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF00FF9A))
            .background(Color(0xFF1A1A1A))
            .padding(12.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onToggleExpand() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = logTitle,
                    color = Color(0xFFF9E97A),
                    fontSize = 15.sp
                )
                Text(
                    text = "FROM: $friendName",
                    color = Color(0xFF6CFF6C),
                    fontSize = 11.sp
                )
            }

            Text(
                text = if (isExpanded) "▲" else "▼",
                color = Color(0xFF00FF9A),
                fontSize = 14.sp
            )
        }

        if (isExpanded) {
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2A2A2A))
                    .padding(10.dp)
            ) {
                Text(
                    text = logText,
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}