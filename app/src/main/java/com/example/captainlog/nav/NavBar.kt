package com.example.captainlog.nav

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
fun CaptainTerminalNavBar(
    currentPage: CaptainPage,
    onSelectPage: (CaptainPage) -> Unit,
    onLogout: () -> Unit
) {
    val border = Color(0xFF8A8A8A)
    val textActive = Color(0xFFF9E97A)
    val textInactive = Color(0xFF6C6C6C)

    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .navigationBarsPadding()
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(border)
        )

        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                title = "RECORD",
                page = CaptainPage.LOG,
                isActive = currentPage == CaptainPage.LOG,
                onSelect = { onSelectPage(CaptainPage.LOG) },
                activeColor = textActive,
                inactiveColor = textInactive,
                borderColor = border
            )

            NavItem(
                title = "ROOM",
                page = CaptainPage.USER,
                isActive = currentPage == CaptainPage.USER,
                onSelect = { onSelectPage(CaptainPage.USER) },
                activeColor = textActive,
                inactiveColor = textInactive,
                borderColor = border
            )

            NavItem(
                title = "STARLINK",
                page = CaptainPage.SOCIAL,
                isActive = currentPage == CaptainPage.SOCIAL,
                onSelect = { onSelectPage(CaptainPage.SOCIAL) },
                activeColor = textActive,
                inactiveColor = textInactive,
                borderColor = border
            )

            Box(
                Modifier
                    .width(90.dp)
                    .height(36.dp)
                    .background(Color.Black)
                    .border(1.dp, border)
                    .clickable { onLogout() },
                contentAlignment = Alignment.Center
            ) {
                Text("LOG OUT", color = textInactive, fontSize = 14.sp)
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(border)
        )
    }
}

@Composable
private fun NavItem(
    title: String,
    page: CaptainPage,
    isActive: Boolean,
    onSelect: () -> Unit,
    activeColor: Color,
    inactiveColor: Color,
    borderColor: Color
) {
    Box(
        Modifier
            .width(90.dp)
            .height(46.dp)
            .background(Color.Black)
            .border(
                1.dp,
                if (isActive) activeColor else borderColor
            )
            .clickable { onSelect() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isActive) activeColor else inactiveColor,
            fontSize = 14.sp
        )
    }
}