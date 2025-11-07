package com.example.captainlog

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.captainlog.auth.LoginScreen
import com.example.captainlog.auth.UserViewModel
import com.example.captainlog.log.LogScreen
import com.example.captainlog.log.LogViewModel
import com.example.captainlog.nav.CaptainPage
import com.example.captainlog.nav.NavViewModel
import com.example.captainlog.room.RoomScreen
import com.example.captainlog.room.RoomViewModel
import com.example.captainlog.social.SocialScreen
import com.example.captainlog.social.SocialViewModel
import java.io.File

class MainActivity : ComponentActivity() {

    private var micGranted = false
    private val micLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            micGranted = granted
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Initialize all ViewModels in MainActivity
            val userVM: UserViewModel = viewModel()
            val navVM: NavViewModel = viewModel()
            val logVM: LogViewModel = viewModel()
            val roomVM: RoomViewModel = viewModel()
            val socialVM: SocialViewModel = viewModel()

            // Collect User states
            val loggedIn by userVM.isLoggedIn.collectAsState()
            val isRegistering by userVM.isRegistering.collectAsState()
            val userName by userVM.userName.collectAsState()
            val userLoading by userVM.loading.collectAsState()
            val userError by userVM.error.collectAsState()

            // Collect Nav states
            val currentPage by navVM.currentPage.collectAsState()

            // Collect Log states
            val isRecording by logVM.isRecording.collectAsState()
            val audioAvailable by logVM.audioAvailable.collectAsState()
            val amplitude by logVM.amplitude.collectAsState()
            val amplitudeHistory by logVM.amplitudeHistory.collectAsState()
            val transcribedText by logVM.transcribedText.collectAsState()
            val isPlaying by logVM.isPlaying.collectAsState()
            val saveStatus by logVM.saveStatus.collectAsState()

            // Collect Room states
            val roomSearchQuery by roomVM.searchQuery.collectAsState()
            val logTitles by roomVM.logTitles.collectAsState()
            val logTexts by roomVM.logTexts.collectAsState()
            val expandedLogId by roomVM.expandedLogId.collectAsState()
            val isPlayingLogId by roomVM.isPlayingLogId.collectAsState()
            val sharedToFriends by roomVM.sharedToFriends.collectAsState()

            // Collect Social states
            val socialSearchQuery by socialVM.searchQuery.collectAsState()
            val searchResult by socialVM.searchResult.collectAsState()
            val friends by socialVM.friends.collectAsState()
            val sharedLogs by socialVM.sharedLogs.collectAsState()
            val receivedLogs by socialVM.receivedLogs.collectAsState()
            val expandedReceivedLog by socialVM.expandedReceivedLog.collectAsState()
            val statusMessage by socialVM.statusMessage.collectAsState()
            val socialLoading by socialVM.loading.collectAsState()

            val micState = remember { mutableStateOf(micGranted) }

            if (!loggedIn) {
                // Login/Register Screen
                LoginScreen(
                    isRegistering = isRegistering,
                    loading = userLoading,
                    error = userError,
                    onLogin = { username, password ->
                        userVM.login(username, password)
                    },
                    onRegister = { username, password ->
                        userVM.register(username, password)
                    },
                    onSwitchToRegister = {
                        userVM.switchModeToRegister()
                    },
                    onSwitchToLogin = {
                        userVM.switchModeToLogin()
                    }
                )
            } else {
                // Main App Screen
                CaptainMainScreen(
                    currentPage = currentPage,
                    username = userName,
                    onSelectPage = { page ->
                        navVM.navigate(page)
                    },
                    onLogout = {
                        userVM.logout()
                        navVM.navigate(CaptainPage.LOG)
                    }
                ) {
                    when (currentPage) {
                        CaptainPage.LOG -> {
                            val file = File(cacheDir, "captain_record.wav")
                            val saveDir = File(getExternalFilesDir(null), "CaptainLogs")

                            LogScreen(
                                username = userName,
                                isRecording = isRecording,
                                audioAvailable = audioAvailable,
                                amplitude = amplitude,
                                amplitudeHistory = amplitudeHistory,
                                transcribedText = transcribedText,
                                isPlaying = isPlaying,
                                saveStatus = saveStatus,
                                haveMicPermission = micState.value,
                                onStartRecording = {
                                    if (!micState.value) {
                                        micLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        micState.value = micGranted
                                    } else {
                                        logVM.startRecording(file)
                                    }
                                },
                                onStopRecording = {
                                    logVM.stopRecording()
                                },
                                onTranscribe = {
                                    logVM.transcribe()
                                },
                                onPlayback = {
                                    if (isPlaying) {
                                        logVM.stopPlayback()
                                    } else {
                                        logVM.playAudio()
                                    }
                                },
                                onSave = {
                                    logVM.saveAudio(saveDir)
                                }
                            )
                        }

                        CaptainPage.USER -> {
                            RoomScreen(
                                searchQuery = roomSearchQuery,
                                logTitles = logTitles,
                                logTexts = logTexts,
                                expandedLogId = expandedLogId,
                                isPlayingLogId = isPlayingLogId,
                                friends = friends,
                                sharedToFriends = sharedToFriends,
                                onSearchQueryChange = { query ->
                                    roomVM.updateSearchQuery(query)
                                },
                                onToggleExpansion = { logId ->
                                    roomVM.toggleLogExpansion(logId)
                                },
                                onTogglePlayback = { logId ->
                                    roomVM.togglePlayback(logId)
                                },
                                onShareLog = { logTitle, friendName ->
                                    socialVM.shareLog(friendName, logTitle)
                                },
                                onAddSharedFriend = { friendName ->
                                    roomVM.addSharedFriend(friendName)
                                },
                                onClearSharedFriends = {
                                    roomVM.clearSharedFriends()
                                }
                            )
                        }

                        CaptainPage.SOCIAL -> {
                            SocialScreen(
                                searchQuery = socialSearchQuery,
                                searchResult = searchResult,
                                friends = friends,
                                sharedLogs = sharedLogs,
                                receivedLogs = receivedLogs,
                                expandedReceivedLog = expandedReceivedLog,
                                statusMessage = statusMessage,
                                loading = socialLoading,
                                onSearchQueryChange = { query ->
                                    socialVM.updateSearchQuery(query)
                                },
                                onSearchUser = {
                                    socialVM.searchUser(socialSearchQuery)
                                },
                                onAddFriend = { username ->
                                    socialVM.addFriend(username)
                                },
                                onRemoveFriend = { username ->
                                    socialVM.removeFriend(username)
                                },
                                onClearSearchResult = {
                                    socialVM.clearSearchResult()
                                },
                                onToggleReceivedLog = { friendName, logTitle ->
                                    socialVM.toggleReceivedLog(friendName, logTitle)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}