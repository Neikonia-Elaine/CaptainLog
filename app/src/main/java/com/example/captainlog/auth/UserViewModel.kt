package com.example.captainlog.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _isRegistering = MutableStateFlow(false)
    val isRegistering: StateFlow<Boolean> = _isRegistering

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _token = MutableStateFlow("")
    val token: StateFlow<String> = _token

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun switchModeToRegister() {
        _isRegistering.value = true
        _error.value = ""
    }

    fun switchModeToLogin() {
        _isRegistering.value = false
        _error.value = ""
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = ""
            delay(1200) // fake API delay

            // Prototype: accept any username/password
            _userName.value = username.ifBlank { "Captain" }
            _token.value = "FAKE_TOKEN_${System.currentTimeMillis()}"
            _isLoggedIn.value = true

            _loading.value = false
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = ""
            delay(1200) // fake API delay

            // Save username (not required but looks real)
            _userName.value = username.ifBlank { "Captain" }
            _token.value = "REGISTER_FAKE_${System.currentTimeMillis()}"

            // Go back to login mode
            _isRegistering.value = false
            _error.value = "CAPTAIN ID CREATED – PLEASE LOG IN"

            _loading.value = false
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _userName.value = ""
        _token.value = ""
        _error.value = ""
        _loading.value = false
    }
}