package com.example.captainlog.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SocialViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResult = MutableStateFlow("")
    val searchResult: StateFlow<String> = _searchResult

    private val _friends = MutableStateFlow(listOf("Captain Kirk", "Captain Picard"))
    val friends: StateFlow<List<String>> = _friends

    private val _sharedLogs = MutableStateFlow(
        mapOf(
            "Captain Kirk" to listOf("Log 1", "Log 2"),
            "Captain Picard" to listOf("Log 3")
        )
    )
    val sharedLogs: StateFlow<Map<String, List<String>>> = _sharedLogs

    // Logs received from friends with full text content
    private val _receivedLogs = MutableStateFlow(
        mapOf(
            "Captain Kirk" to mapOf(
                "Log 1" to "Encountered a temporal anomaly near the Neutral Zone. Shields holding at 87%. Investigating further.",
                "Log 2" to "First contact protocol successful with new species. Establishing diplomatic channels."
            ),
            "Captain Picard" to mapOf(
                "Log 3" to "Diplomatic negotiations completed. Peace treaty signed with the Romulan delegation."
            )
        )
    )
    val receivedLogs: StateFlow<Map<String, Map<String, String>>> = _receivedLogs

    private val _statusMessage = MutableStateFlow("")
    val statusMessage: StateFlow<String> = _statusMessage

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _expandedReceivedLog = MutableStateFlow<Pair<String, String>?>(null)
    val expandedReceivedLog: StateFlow<Pair<String, String>?> = _expandedReceivedLog

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun searchUser(username: String) {
        viewModelScope.launch {
            _loading.value = true
            _searchResult.value = ""
            delay(800) // Fake API delay

            if (username.isNotBlank()) {
                _searchResult.value = username
            }
            _loading.value = false
        }
    }

    fun addFriend(username: String) {
        viewModelScope.launch {
            _loading.value = true
            delay(600)

            if (!_friends.value.contains(username)) {
                _friends.value = _friends.value + username
                _statusMessage.value = "FRIEND ADDED: $username"
            } else {
                _statusMessage.value = "ALREADY IN FRIEND LIST"
            }

            _searchResult.value = ""
            _loading.value = false

            delay(2000)
            _statusMessage.value = ""
        }
    }

    fun removeFriend(username: String) {
        viewModelScope.launch {
            _loading.value = true
            delay(600)

            _friends.value = _friends.value.filter { it != username }
            _statusMessage.value = "FRIEND REMOVED: $username"

            _loading.value = false

            delay(2000)
            _statusMessage.value = ""
        }
    }

    fun isFriend(username: String): Boolean {
        return _friends.value.contains(username)
    }

    fun shareLog(friendUsername: String, logTitle: String) {
        viewModelScope.launch {
            _loading.value = true
            delay(600)

            _statusMessage.value = "LOG SHARED TO: $friendUsername"

            _loading.value = false

            delay(2000)
            _statusMessage.value = ""
        }
    }

    fun clearSearchResult() {
        _searchResult.value = ""
    }

    fun toggleReceivedLog(friendName: String, logTitle: String) {
        val key = friendName to logTitle
        _expandedReceivedLog.value = if (_expandedReceivedLog.value == key) null else key
    }
}