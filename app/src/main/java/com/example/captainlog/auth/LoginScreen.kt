package com.example.captainlog.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable

@Composable
fun LoginScreen(
    isRegistering: Boolean,
    loading: Boolean,
    error: String,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String) -> Unit,
    onSwitchToRegister: () -> Unit,
    onSwitchToLogin: () -> Unit
) {
    var userInput by remember { mutableStateOf("") }
    var passInput by remember { mutableStateOf("") }

    // Clear inputs when switching modes
    LaunchedEffect(isRegistering) {
        userInput = ""
        passInput = ""
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header title
        Text(
            if (isRegistering) "CAPTAIN ENLISTMENT"
            else "STARSHIP TERMINAL",
            color = Color(0xFF6CFF6C),
            fontSize = 20.sp
        )

        Spacer(Modifier.height(20.dp))

        // Username input
        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = {
                Text(
                    if (isRegistering) "Create Captain ID" else "Captain ID",
                    color = Color(0xFF6CFF6C)
                )
            },
            textStyle = LocalTextStyle.current.copy(color = Color(0xFF6CFF6C)),
            enabled = !loading
        )

        Spacer(Modifier.height(12.dp))

        // Password input
        OutlinedTextField(
            value = passInput,
            onValueChange = { passInput = it },
            label = {
                Text(
                    if (isRegistering) "Create Access Code" else "Access Code",
                    color = Color(0xFF6CFF6C)
                )
            },
            textStyle = LocalTextStyle.current.copy(color = Color(0xFF6CFF6C)),
            enabled = !loading
        )

        Spacer(Modifier.height(20.dp))

        // Login / Register Button
        if (loading) {
            Text(
                if (isRegistering) "INITIATING ID SEAL…"
                else "VERIFYING IDENTITY…",
                color = Color(0xFF6CFF6C)
            )
        } else {
            CaptainButton(
                text = if (isRegistering) "ENLIST" else "REQUEST ACCESS",
                onClick = {
                    if (isRegistering) {
                        onRegister(userInput, passInput)
                    } else {
                        onLogin(userInput, passInput)
                    }
                }
            )
        }

        // Error / Success message
        if (error.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = error,
                color = if (error.contains("CREATED")) Color(0xFF6CFF6C) else Color.Red
            )
        }

        Spacer(Modifier.height(14.dp))

        // Switch mode link
        TextButton(
            onClick = {
                if (isRegistering) {
                    onSwitchToLogin()
                } else {
                    onSwitchToRegister()
                }
            },
            enabled = !loading
        ) {
            Text(
                if (isRegistering) "Return to Starship"
                else "No Captain ID? Apply Here",
                color = Color(0xFF6CFF6C),
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
fun CaptainButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var pressed by remember { mutableStateOf(false) }

    val borderColor = if (pressed) Color(0xFFBFAE48) else Color(0xFF8A8A8A)
    val textColor = if (enabled) Color(0xFFF9E97A) else Color(0xFF6C6C6C)

    Box(
        modifier = modifier
            .height(42.dp)
            .width(220.dp)
            .background(Color.Black)
            .border(1.dp, borderColor)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = textColor, fontSize = 15.sp)
    }
}