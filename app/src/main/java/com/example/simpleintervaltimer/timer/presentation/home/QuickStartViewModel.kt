package com.example.simpleintervaltimer.timer.presentation.home

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
                _uiState.value = UiState(timeInterval = it.quickStartTimeInterval)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

    data class UiState(
        val isLoading: Boolean = false,
        val showNameInput: Boolean = false,
        val timeInterval: TimeInterval = TimeInterval(30_000, 30_000, 10)
    )

    fun setInterval(timeInterval: TimeInterval) {
        _uiState.value = _uiState.value.copy(timeInterval = timeInterval)
    }

    fun showNameInput() {
        _uiState.value = _uiState.value.copy(showNameInput = true)
    }

    fun dismissNameInput() {
        _uiState.value = _uiState.value.copy(showNameInput = false)
    }

    fun saveInterval(intervalName: String) {
        dismissNameInput()
        persistQuickStartTimeInterval()
        val intervalName = if (intervalName.isBlank()) {
            DEFAULT_TIME_INTERVAL_NAME
        } else intervalName
        val timeInterval = _uiState.value.timeInterval
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
        persistQuickStartTimeInterval()
        onStartTimer(_uiState.value.timeInterval)
    }

    private fun persistQuickStartTimeInterval() {
        viewModelScope.launch {
            timerSettingsRepository.updateQuickStartTimeInterval(_uiState.value.timeInterval)
        }
    }

    companion object {
        const val DEFAULT_TIME_INTERVAL_NAME = "My Time Interval"
    }
}
