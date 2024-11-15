package com.example.simpleintervaltimer.timer.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simpleintervaltimer.timer.data.db.realm_objects.StoredTimeInterval
import com.example.simpleintervaltimer.timer.data.repositories.StoredTimeIntervalRepository
import com.example.simpleintervaltimer.timer.data.repositories.TimerSettingsRepository
import com.example.simpleintervaltimer.timer.domain.models.TimeInterval
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuickStartViewModelFactory(
    private val timerSettingsRepository: TimerSettingsRepository,
    private val storedTimeIntervalRepository: StoredTimeIntervalRepository = StoredTimeIntervalRepository()
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = QuickStartViewModel(timerSettingsRepository, storedTimeIntervalRepository) as T
}

class QuickStartViewModel(
    private val timerSettingsRepository: TimerSettingsRepository,
    private val storedTimeIntervalRepository: StoredTimeIntervalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            timerSettingsRepository.timerSettingsFlow.collect {
                _uiState.value = UiState.fromTimeInterval(it.quickStartTimeInterval)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

    data class UiState(
        val isLoading: Boolean = false,
        val showNameInput: Boolean = false,
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

        companion object {
            fun fromTimeInterval(timeInterval: TimeInterval): UiState {
                val workTime = timeInterval.workTime / 1000
                val restTime = timeInterval.restTime / 1000
                return UiState(
                    intervalCount = timeInterval.intervals.toString(),
                    workIntervalMinutes = (workTime / 60).toString(),
                    workIntervalSeconds = (workTime % 60).toString(),
                    restIntervalMinutes = (restTime / 60).toString(),
                    restIntervalSeconds = (restTime % 60).toString()
                )
            }
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

    fun showNameInput() {
        _uiState.value = _uiState.value.copy(showNameInput = true)
    }

    fun dismissNameInput() {
        _uiState.value = _uiState.value.copy(showNameInput = false)
    }

    fun saveInterval(intervalName: String) {
        validateInput()
        persistQuickStartTimeInterval()
        val intervalName = if (intervalName.isBlank()) {
            DEFAULT_TIME_INTERVAL_NAME
        } else intervalName
        val timeInterval = _uiState.value.getTimeInterval()
        val storedTimeInterval = StoredTimeInterval().apply {
            name = intervalName
            workTime = timeInterval.workTime
            restTime = timeInterval.restTime
            intervals = timeInterval.intervals
        }
        viewModelScope.launch {
            storedTimeIntervalRepository.addStoredTimeInterval(storedTimeInterval)
        }
    }

    fun startTimer(onStartTimer: (timeInterval: TimeInterval) -> Unit) {
        validateInput()
        persistQuickStartTimeInterval()
        onStartTimer(_uiState.value.getTimeInterval())
    }

    private fun persistQuickStartTimeInterval() {
        viewModelScope.launch {
            timerSettingsRepository.updateQuickStartTimeInterval(_uiState.value.getTimeInterval())
        }
    }

    companion object {
        const val DEFAULT_TIME_INTERVAL_NAME = "My Time Interval"
    }
}
