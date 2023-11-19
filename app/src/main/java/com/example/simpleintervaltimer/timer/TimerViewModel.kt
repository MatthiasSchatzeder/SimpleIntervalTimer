package com.example.simpleintervaltimer.timer

import android.os.CountDownTimer
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.simpleintervaltimer.timer.TimerViewModel.TimerState.DONE
import com.example.simpleintervaltimer.timer.TimerViewModel.TimerState.INIT
import com.example.simpleintervaltimer.timer.TimerViewModel.TimerState.PAUSE
import com.example.simpleintervaltimer.timer.TimerViewModel.TimerState.WORK
import com.example.simpleintervaltimer.timer.data.TimeInterval

class TimerViewModel : ViewModel() {
    private lateinit var timer: CountDownTimer
    private lateinit var timeInterval: TimeInterval

    private val _time: MutableLiveData<Long> = MutableLiveData()
    private val _remainingIntervals: MutableLiveData<Int> = MutableLiveData(0)
    private val _timerState: MutableLiveData<TimerState> = MutableLiveData(INIT)
    private val _isTimerRunning: MutableLiveData<Boolean> = MutableLiveData(false)

    val formattedTime: LiveData<String> = _time.map {
        if (_timerState.value == DONE) return@map "Done"
        val millis: Long = it % 1000
        val second: Long = it / 1000 % 60
        val minute: Long = it / (1000 * 60) % 60
        "%d,%d".format(second, millis / 100)
    }

    val timeProgress: LiveData<Float> = _time.map {
        val maxValue: Float = when (_timerState.value) {
            INIT -> DEFAULT_START_TIME.toFloat()
            WORK -> timeInterval.workTime.toFloat()
            PAUSE -> timeInterval.pauseTime.toFloat()
            DONE, null -> return@map 1.0f
        }
        1 - (it.toFloat() / maxValue)
    }

    val progressColor: LiveData<Color> = _timerState.map {
        when (_timerState.value) {
            INIT -> Color.Yellow
            WORK -> Color.Green
            PAUSE -> Color.Blue
            DONE, null -> Color.Cyan
        }
    }

    val remainingIntervals: LiveData<String> = _remainingIntervals.map {
        if (it == 0) "" else "$it"
    }

    val progressState: LiveData<String> = _timerState.map {
        when (it) {
            INIT -> "Prepare"
            WORK -> "Work"
            PAUSE -> "Pause"
            DONE, null -> ""
        }
    }

    val pauseResumeButtonVisible: LiveData<Boolean> = _timerState.map {
        when (it) {
            INIT, WORK, PAUSE -> true
            DONE, null -> false
        }
    }

    val startStopButtonText: LiveData<String> = _isTimerRunning.map {
        if (it) "Stop" else "Resume"
    }

    fun startTimer(timeInterval: TimeInterval) {
        this.timeInterval = timeInterval
        _time.value = timeInterval.workTime
        _remainingIntervals.value = timeInterval.intervals
        startTimer(DEFAULT_START_TIME)
    }

    private fun startTimer(startTime: Long) {
        timer = object : CountDownTimer(startTime, DEFAULT_TICK_TIME) {
            override fun onTick(millisUntilFinished: Long) {
                _time.value = millisUntilFinished
            }

            override fun onFinish() {
                _time.value = 0
                _isTimerRunning.value = false
                evaluateNextInterval()
            }
        }
        _isTimerRunning.value = true
        timer.start()
    }

    private fun evaluateNextInterval() {
        when (_timerState.value) {
            INIT -> {
                _timerState.value = WORK
                startTimer(timeInterval.workTime)
            }

            WORK -> {
                _remainingIntervals.value = _remainingIntervals.value?.minus(1)
                if (_remainingIntervals.value == 0) {
                    _timerState.value = DONE
                    _time.value = -1 // set _time to trigger change in timeProgress
                } else {
                    _timerState.value = PAUSE
                    startTimer(timeInterval.pauseTime)
                }
            }

            PAUSE -> {
                _timerState.value = WORK
                startTimer(timeInterval.workTime)
            }

            DONE, null -> {
                // do nothing
            }
        }
    }

    fun pauseOrResumeTimer() {
        if (_isTimerRunning.value == true) {
            timer.cancel()
            _isTimerRunning.value = false
        } else {
            startTimer(_time.value!!)
        }
    }

    fun stopTimer() {
        timer.cancel()
        _isTimerRunning.value = false
        _time.value = 0
    }

    companion object {
        private const val DEFAULT_START_TIME = 5_000L
        private const val DEFAULT_TICK_TIME = 10L
    }

    private enum class TimerState {
        INIT, WORK, PAUSE, DONE
    }
}