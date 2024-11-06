package com.example.simpleintervaltimer.timer.presentation

import android.os.CountDownTimer
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simpleintervaltimer.timer.data.TimeInterval
import com.example.simpleintervaltimer.timer.presentation.TimerViewModel.IntervalState.DONE
import com.example.simpleintervaltimer.timer.presentation.TimerViewModel.IntervalState.INIT
import com.example.simpleintervaltimer.timer.presentation.TimerViewModel.IntervalState.REST
import com.example.simpleintervaltimer.timer.presentation.TimerViewModel.IntervalState.WORK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimerViewModelFactory(private val timeInterval: TimeInterval) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = TimerViewModel(timeInterval) as T
}

class TimerViewModel(private val timeInterval: TimeInterval) : ViewModel() {
    private lateinit var timer: CountDownTimer

    private val _uiState: MutableStateFlow<TimerUiState> = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState

    fun startTimer() {
        _uiState.value = _uiState.value.copy(
            remainingTime = DEFAULT_PREPARE_TIME,
            remainingIntervals = timeInterval.intervals
        )
        startTimer(DEFAULT_PREPARE_TIME)
    }

    private fun startTimer(startTime: Long) {
        timer = object : CountDownTimer(startTime, DEFAULT_TICK_TIME) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.value = _uiState.value.copy(
                    remainingTime = millisUntilFinished,
                    percentageDone = calculatePercentageDone(millisUntilFinished)
                )
            }

            override fun onFinish() {
                _uiState.value = _uiState.value.copy(
                    remainingTime = 0,
                    isTimerRunning = false,
                    percentageDone = 1.0f
                )
                onTimerFinished()
            }
        }.start()
        _uiState.value = _uiState.value.copy(isTimerRunning = true)
    }

    private fun calculatePercentageDone(millisUntilFinished: Long): Float {
        val maxValue: Float = when (_uiState.value.intervalState) {
            INIT -> DEFAULT_PREPARE_TIME.toFloat()
            WORK -> timeInterval.workTime.toFloat()
            REST -> timeInterval.restTime.toFloat()
            DONE -> 1.0f
        }
        return 1 - (millisUntilFinished.toFloat() / maxValue)
    }

    private fun onTimerFinished() {
        when (_uiState.value.intervalState) {
            INIT -> {
                _uiState.value = _uiState.value.copy(intervalState = WORK)
                startTimer(timeInterval.workTime)
            }

            WORK -> {
                val state = if (_uiState.value.remainingIntervals == 1) {
                    DONE
                } else {
                    startTimer(timeInterval.restTime)
                    REST
                }
                _uiState.value = _uiState.value.copy(
                    intervalState = state,
                    remainingIntervals = _uiState.value.remainingIntervals - 1
                )
            }

            REST -> {
                _uiState.value = _uiState.value.copy(intervalState = WORK)
                startTimer(timeInterval.workTime)
            }

            DONE -> {
                // nothing to do
            }
        }
    }

    fun pauseOrResumeTimer() {
        if (_uiState.value.isTimerRunning) {
            timer.cancel()
            _uiState.value = _uiState.value.copy(isTimerRunning = false)
        } else {
            startTimer(_uiState.value.remainingTime)
        }
    }

    fun stopTimer() {
        timer.cancel()
        _uiState.value = _uiState.value.copy(remainingTime = 0, isTimerRunning = false)
    }

    companion object {
        private const val DEFAULT_PREPARE_TIME = 5_000L
        private const val DEFAULT_TICK_TIME = 10L
    }

    enum class IntervalState {
        INIT, WORK, REST, DONE;

        fun toStateString(): String = when (this) {
            INIT -> "Prepare"
            WORK -> "Work"
            REST -> "Rest"
            DONE -> "Done"
        }

        fun toStateColor() = when (this) {
            INIT -> Color.Yellow
            WORK -> Color.Green
            REST -> Color.Blue
            DONE -> Color.Cyan
        }
    }

    data class TimerUiState(
        val remainingTime: Long = DEFAULT_PREPARE_TIME,
        val percentageDone: Float = 0.0f,
        val remainingIntervals: Int = 0,
        val intervalState: IntervalState = INIT,
        val isTimerRunning: Boolean = false,
    ) {
        fun getRemainingTimeFormatted(): String {
            if (intervalState == DONE) return "Done"
            val millis = remainingTime % 1000
            val second = remainingTime / 1000 % 60
            val minute = remainingTime / (1000 * 60) % 60
            return if (minute == 0L) {
                "%d,%d".format(second, millis / 100)
            } else {
                "%d:%d,%d".format(minute, second, millis / 100)
            }
        }

        fun getResumeStopButtonText(): String {
            return if (isTimerRunning) "Stop" else "Resume"
        }

        fun isPauseResumeButtonVisible(): Boolean {
            return when (intervalState) {
                INIT, WORK, REST -> true
                DONE -> false
            }
        }

        fun getRemainingIntervalsText(): String {
            if (remainingIntervals == 1) return "Last Interval"
            if (remainingIntervals <= 0) return ""
            return remainingIntervals.toString()
        }
    }
}
