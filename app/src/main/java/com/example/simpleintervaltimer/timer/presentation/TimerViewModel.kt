package com.example.simpleintervaltimer.timer.presentation

import android.os.CountDownTimer
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.simpleintervaltimer.timer.data.TimeInterval
import com.example.simpleintervaltimer.timer.data.TimerSoundDefinition
import com.example.simpleintervaltimer.timer.presentation.TimerViewModel.IntervalState.DONE
import com.example.simpleintervaltimer.timer.presentation.TimerViewModel.IntervalState.INIT
import com.example.simpleintervaltimer.timer.presentation.TimerViewModel.IntervalState.REST
import com.example.simpleintervaltimer.timer.presentation.TimerViewModel.IntervalState.WORK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimerViewModelFactory(
    private val timeInterval: TimeInterval,
    private val player: ExoPlayer,
    private val timerSoundDefinition: TimerSoundDefinition
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = TimerViewModel(timeInterval, player, timerSoundDefinition) as T
}

class TimerViewModel(
    private val timeInterval: TimeInterval,
    private val player: ExoPlayer,
    private val soundDefinition: TimerSoundDefinition
) : ViewModel() {
    private lateinit var timer: CountDownTimer
    private var soundTriggerSecond: Int = INITIAL_SOUND_TRIGGER_SECOND

    private val _uiState: MutableStateFlow<TimerUiState> = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        player.release()
    }

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
                playCountDownSound(millisUntilFinished)
            }

            private fun playCountDownSound(millisUntilFinished: Long) {
                if (millisUntilFinished.getDisplayMinutes() > 0) return
                val millis = millisUntilFinished.getDisplayMillis()
                val seconds = millisUntilFinished.getDisplaySeconds()
                if (seconds == soundTriggerSecond.toLong() && millis < SOUND_TRIGGER_MILLIS_THRESHOLD) {
                    val sound = if (_uiState.value.intervalState == WORK) soundDefinition.preEndWorkIntervalSound
                    else soundDefinition.preEndRestIntervalSound
                    playSound(sound)
                    soundTriggerSecond = if (soundTriggerSecond == 1) INITIAL_SOUND_TRIGGER_SECOND
                    else soundTriggerSecond - 1
                }
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
        val maxValue: Long = when (_uiState.value.intervalState) {
            INIT -> DEFAULT_PREPARE_TIME
            WORK -> timeInterval.workTime
            REST -> timeInterval.restTime
            DONE -> 1
        }
        return 1 - (millisUntilFinished.toFloat() / maxValue.toFloat())
    }

    private fun onTimerFinished() {
        when (_uiState.value.intervalState) {
            INIT, REST -> {
                playSound(soundDefinition.endRestIntervalSound)
                _uiState.value = _uiState.value.copy(intervalState = WORK)
                startTimer(timeInterval.workTime)
            }

            WORK -> {
                val state = if (_uiState.value.remainingIntervals == 1) {
                    playSound(soundDefinition.finishSound)
                    DONE
                } else {
                    playSound(soundDefinition.endWorkIntervalSound)
                    startTimer(timeInterval.restTime)
                    REST
                }
                _uiState.value = _uiState.value.copy(
                    intervalState = state,
                    remainingIntervals = _uiState.value.remainingIntervals - 1
                )
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

    private fun playSound(sound: MediaItem) {
        player.stop()
        player.setMediaItem(sound)
        player.prepare()
        player.play()
    }

    companion object {
        private const val DEFAULT_PREPARE_TIME: Long = 5_000L
        private const val DEFAULT_TICK_TIME: Long = 10L
        private const val INITIAL_SOUND_TRIGGER_SECOND: Int = 3
        private const val SOUND_TRIGGER_MILLIS_THRESHOLD: Long = 100L
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
            val millis = remainingTime.getDisplayMillis()
            val seconds = remainingTime.getDisplaySeconds()
            val minutes = remainingTime.getDisplayMinutes()
            return if (minutes == 0L) {
                "%d,%d".format(seconds, millis / 100)
            } else {
                "%d:%d,%d".format(minutes, seconds, millis / 100)
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

private fun Long.getDisplayMillis() = this % 1000
private fun Long.getDisplaySeconds() = this / 1000 % 60
private fun Long.getDisplayMinutes() = this / (1000 * 60) % 60
