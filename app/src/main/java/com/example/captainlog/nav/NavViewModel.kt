package com.example.captainlog.nav

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class CaptainPage {
    LOG, USER, SOCIAL
}

class NavViewModel : ViewModel() {
    private val _currentPage = MutableStateFlow(CaptainPage.LOG)
    val currentPage: StateFlow<CaptainPage> = _currentPage

    fun navigate(page: CaptainPage) {
        _currentPage.value = page
    }
}
