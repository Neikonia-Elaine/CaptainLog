package com.example.captainlog.room

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RoomViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _expandedLogId = MutableStateFlow<String?>(null)
    val expandedLogId: StateFlow<String?> = _expandedLogId

    private val _isPlayingLogId = MutableStateFlow<String?>(null)
    val isPlayingLogId: StateFlow<String?> = _isPlayingLogId

    private val _sharedToFriends = MutableStateFlow<Set<String>>(emptySet())
    val sharedToFriends: StateFlow<Set<String>> = _sharedToFriends

    // Fake log data for testing UI
    private val _logTitles = MutableStateFlow(listOf("Log 1", "Log 2", "Log 3"))
    val logTitles: StateFlow<List<String>> = _logTitles

    private val _logTexts = MutableStateFlow(
        mapOf(
            "Log 1" to "Captain's Log, Stardate 2025.11.04. Mission status nominal. All systems operating within parameters.",
            "Log 2" to "Captain's Log, supplemental. Encountered unusual spatial phenomena in sector 7G.",
            "Log 3" to "Captain's Log. Diplomatic mission completed successfully."
        )
    )
    val logTexts: StateFlow<Map<String, String>> = _logTexts

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleLogExpansion(logId: String) {
        _expandedLogId.value = if (_expandedLogId.value == logId) null else logId
    }

    fun togglePlayback(logId: String) {
        _isPlayingLogId.value = if (_isPlayingLogId.value == logId) null else logId
    }

    fun addSharedFriend(friendName: String) {
        _sharedToFriends.value = _sharedToFriends.value + friendName
    }

    fun clearSharedFriends() {
        _sharedToFriends.value = emptySet()
    }
}