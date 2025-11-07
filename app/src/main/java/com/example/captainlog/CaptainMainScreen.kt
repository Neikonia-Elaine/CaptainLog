package com.example.captainlog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.captainlog.nav.CaptainPage
import com.example.captainlog.nav.CaptainTerminalNavBar

@Composable
fun CaptainMainScreen(
    currentPage: CaptainPage,
    username: String,
    onSelectPage: (CaptainPage) -> Unit,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(Modifier.fillMaxSize()) {

        // Main content slot (page content goes here)
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            content()
        }

        // Navigation bar with state-driven UI
        CaptainTerminalNavBar(
            currentPage = currentPage,
            onSelectPage = onSelectPage,
            onLogout = onLogout
        )
    }
}