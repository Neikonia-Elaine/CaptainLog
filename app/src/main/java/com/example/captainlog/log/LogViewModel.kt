package com.example.captainlog.log

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogViewModel : ViewModel() {

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private var audioFile: File? = null
    private var savedAudioFile: File? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _audioAvailable = MutableStateFlow(false)
    val audioAvailable: StateFlow<Boolean> = _audioAvailable

    private val _amplitude = MutableStateFlow(0)
    val amplitude: StateFlow<Int> = _amplitude

    private val _amplitudeHistory = MutableStateFlow<List<Int>>(emptyList())
    val amplitudeHistory: StateFlow<List<Int>> = _amplitudeHistory

    private val _transcribedText = MutableStateFlow("")
    val transcribedText: StateFlow<String> = _transcribedText

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _saveStatus = MutableStateFlow("")
    val saveStatus: StateFlow<String> = _saveStatus

    fun startRecording(file: File) {
        try {
            // Stop any playing audio first
            stopPlayback()

            audioFile = file
            _amplitudeHistory.value = emptyList()
            _transcribedText.value = ""

            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            _isRecording.value = true

            viewModelScope.launch {
                while (_isRecording.value) {
                    val amp = recorder?.maxAmplitude ?: 0
                    _amplitude.value = amp
                    _amplitudeHistory.value = _amplitudeHistory.value + amp
                    delay(80)
                }
            }

            Log.d("LogViewModel", "Recording started: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("LogViewModel", "Failed to start recording", e)
            _isRecording.value = false
        }
    }

    fun stopRecording() {
        try {
            _isRecording.value = false
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            _audioAvailable.value = true
            _amplitude.value = 0

            Log.d("LogViewModel", "Recording stopped: ${audioFile?.absolutePath}")
        } catch (e: Exception) {
            Log.e("LogViewModel", "Failed to stop recording", e)
        }
    }

    fun playAudio() {
        val fileToPlay = savedAudioFile ?: audioFile

        if (fileToPlay == null || !fileToPlay.exists()) {
            Log.e("LogViewModel", "No audio file available to play")
            return
        }

        try {
            // Stop any existing playback
            stopPlayback()

            player = MediaPlayer().apply {
                setDataSource(fileToPlay.absolutePath)
                prepare()

                setOnCompletionListener {
                    _isPlaying.value = false
                    Log.d("LogViewModel", "Playback completed")
                }

                setOnErrorListener { _, what, extra ->
                    Log.e("LogViewModel", "MediaPlayer error: what=$what, extra=$extra")
                    _isPlaying.value = false
                    true
                }

                start()
                _isPlaying.value = true
            }

            Log.d("LogViewModel", "Playing audio: ${fileToPlay.absolutePath}")
        } catch (e: Exception) {
            Log.e("LogViewModel", "Failed to play audio", e)
            _isPlaying.value = false
        }
    }

    fun stopPlayback() {
        try {
            player?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            player = null
            _isPlaying.value = false
        } catch (e: Exception) {
            Log.e("LogViewModel", "Failed to stop playback", e)
        }
    }

    fun saveAudio(saveDirectory: File) {
        viewModelScope.launch {
            try {
                if (audioFile == null || !audioFile!!.exists()) {
                    _saveStatus.value = "ERROR: No audio to save"
                    delay(2000)
                    _saveStatus.value = ""
                    return@launch
                }

                // Create save directory if it doesn't exist
                if (!saveDirectory.exists()) {
                    saveDirectory.mkdirs()
                }

                // Generate filename with timestamp
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(Date())
                val fileName = "captain_log_$timestamp.m4a"
                val destinationFile = File(saveDirectory, fileName)

                // Copy the audio file
                audioFile!!.copyTo(destinationFile, overwrite = true)
                savedAudioFile = destinationFile

                _saveStatus.value = "SAVED: $fileName"
                Log.d("LogViewModel", "Audio saved to: ${destinationFile.absolutePath}")

                // Clear status after 3 seconds
                delay(3000)
                _saveStatus.value = ""
            } catch (e: IOException) {
                Log.e("LogViewModel", "Failed to save audio", e)
                _saveStatus.value = "ERROR: Failed to save"
                delay(2000)
                _saveStatus.value = ""
            }
        }
    }

    fun transcribe() {
        viewModelScope.launch {
            _transcribedText.value = "Transcribing..."
            delay(800)
            // Could have a ML API here
            val timestamp = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date())
            _transcribedText.value = "Captain's Log, Stardate $timestamp. " +
                    "This is a Test message"
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPlayback()
        recorder?.release()
    }
}