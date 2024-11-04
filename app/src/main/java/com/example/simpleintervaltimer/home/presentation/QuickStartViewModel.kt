package com.example.simpleintervaltimer.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.simpleintervaltimer.timer.data.TimeInterval
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QuickStartViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    data class UiState(
        val intervalCount: String = "10",
        val workIntervalMinutes: String = "0",
        val workIntervalSeconds: String = "30",
        val restIntervalMinutes: String = "0",
        val restIntervalSeconds: String = "30"
    ) {
        fun getTimeInterval(): TimeInterval {
            val workTime = (workIntervalMinutes.toLong() * 60 + workIntervalSeconds.toLong()) * 1000L
            val restTime = (restIntervalMinutes.toLong() * 60 + restIntervalSeconds.toLong()) * 1000L
            return TimeInterval(workTime, restTime, intervalCount.toInt())
        }
    }

    fun setIntervalCount(count: String) {
        _uiState.value = _uiState.value.copy(intervalCount = count)
    }

    fun setWorkIntervalMinutes(minutes: String) {
        _uiState.value = _uiState.value.copy(workIntervalMinutes = minutes)
    }

    fun setWorkIntervalSeconds(seconds: String) {
        _uiState.value = _uiState.value.copy(workIntervalSeconds = seconds)
    }

    fun setRestIntervalMinutes(minutes: String) {
        _uiState.value = _uiState.value.copy(restIntervalMinutes = minutes)
    }

    fun setRestIntervalSeconds(seconds: String) {
        _uiState.value = _uiState.value.copy(restIntervalSeconds = seconds)
    }

    fun validateInput() {
        var intervalCount = checkAndValidateInput(_uiState.value.intervalCount, 1000, 1)
        var workIntervalMinutes = checkAndValidateInput(_uiState.value.workIntervalMinutes, 99)
        var workIntervalSeconds = checkAndValidateInput(_uiState.value.workIntervalSeconds, 59, if (workIntervalMinutes.toInt() == 0) 1 else 0)
        var restIntervalMinutes = checkAndValidateInput(_uiState.value.restIntervalMinutes, 99)
        var restIntervalSeconds = checkAndValidateInput(_uiState.value.restIntervalSeconds, 59, if (restIntervalMinutes.toInt() == 0) 1 else 0)
        _uiState.value = UiState(
            intervalCount = intervalCount,
            workIntervalMinutes = workIntervalMinutes,
            workIntervalSeconds = workIntervalSeconds,
            restIntervalMinutes = restIntervalMinutes,
            restIntervalSeconds = restIntervalSeconds
        )
    }

    private fun checkAndValidateInput(inputString: String, maxValue: Int, minValue: Int = 0): String {
        try {
            val intValue = inputString.replace("\\s".toRegex(), "").toInt()
            return intValue.coerceIn(minValue, maxValue).toString()
        } catch (exception: Exception) {
            Log.e(
                "QuickStartViewModel::checkAndValidateInput::",
                "Failed to validate input: ${exception.message}"
            )
            return minValue.toString()
        }
    }
}