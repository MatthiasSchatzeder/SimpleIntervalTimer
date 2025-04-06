package com.example.simpleintervaltimer.timer.presentation.components.time_interval_input

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simpleintervaltimer.timer.domain.models.TimeInterval
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimeIntervalInputViewModelFactory(
	val initialTimeInterval: TimeInterval
) : ViewModelProvider.Factory {
	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T = TimeIntervalInputViewModel(initialTimeInterval) as T
}

class TimeIntervalInputViewModel(
	initialTimeInterval: TimeInterval
) : ViewModel() {
	private val _uiState = MutableStateFlow(UiState.fromTimeInterval(initialTimeInterval))
	val uiState: StateFlow<UiState> = _uiState.asStateFlow()

	data class UiState(
		val intervals: String = "10",
		val workMinutes: String = "00",
		val workSeconds: String = "30",
		val restMinutes: String = "00",
		val restSeconds: String = "30"
	) {
		@Throws(NumberFormatException::class)
		fun toTimeInterval(): TimeInterval {
			val workTime = (workMinutes.toLong() * 60 + workSeconds.toLong()) * 1000L
			val restTime = (restMinutes.toLong() * 60 + restSeconds.toLong()) * 1000L
			return TimeInterval(workTime, restTime, intervals.toInt())
		}

		fun validate(): UiState {
			var intervalCount = checkAndValidate(intervals, 1000, 1, 1)
			var workIntervalMinutes = checkAndValidate(workMinutes, 99, 0)
			var workIntervalSeconds = checkAndValidate(workSeconds, 59, if (workIntervalMinutes.toInt() == 0) 1 else 0)
			var restIntervalMinutes = checkAndValidate(restMinutes, 99, 0)
			var restIntervalSeconds = checkAndValidate(restSeconds, 59, if (restIntervalMinutes.toInt() == 0) 1 else 0)
			return UiState(
				intervals = intervalCount,
				workMinutes = workIntervalMinutes,
				workSeconds = workIntervalSeconds,
				restMinutes = restIntervalMinutes,
				restSeconds = restIntervalSeconds
			)
		}

		private fun checkAndValidate(inputString: String, maxValue: Int, minValue: Int, minDigitsCount: Int = 2): String {
			try {
				val intValue = inputString.replace("\\s".toRegex(), "").toInt().coerceIn(minValue, maxValue)
				return "%0${minDigitsCount}d".format(intValue)
			} catch (exception: Exception) {
				Log.e(
					"TimeIntervalInputViewModel::checkAndValidate::",
					"Failed to validate input: ${exception.message}"
				)
				return "%0${minDigitsCount}d".format(minValue)
			}
		}

		companion object {
			fun fromTimeInterval(timeInterval: TimeInterval): UiState {
				val workTime = timeInterval.workTime / 1000
				val restTime = timeInterval.restTime / 1000
				return UiState(
					intervals = timeInterval.intervals.toString(),
					workMinutes = (workTime / 60).toString(),
					workSeconds = (workTime % 60).toString(),
					restMinutes = (restTime / 60).toString(),
					restSeconds = (restTime % 60).toString()
				).validate()
			}
		}
	}

	fun setIntervalCount(count: String) {
		_uiState.value = _uiState.value.copy(intervals = count)
	}

	fun setWorkIntervalMinutes(minutes: String) {
		_uiState.value = _uiState.value.copy(workMinutes = minutes)
	}

	fun setWorkIntervalSeconds(seconds: String) {
		_uiState.value = _uiState.value.copy(workSeconds = seconds)
	}

	fun setRestIntervalMinutes(minutes: String) {
		_uiState.value = _uiState.value.copy(restMinutes = minutes)
	}

	fun setRestIntervalSeconds(seconds: String) {
		_uiState.value = _uiState.value.copy(restSeconds = seconds)
	}

	fun increaseIntervalsByOne() = alterIntervalsBy(1)

	fun decreaseIntervalsByOne() = alterIntervalsBy(-1)

	private fun alterIntervalsBy(value: Int) {
		var intervalCount = _uiState.value.intervals.toInt()
		intervalCount += value
		_uiState.value = _uiState.value.copy(intervals = intervalCount.toString()).validate()
	}

	fun increaseWorkMinutesByOne() = alterWorkMinutesBy(1)

	fun decreaseWorkMinutesByOne() = alterWorkMinutesBy(-1)

	private fun alterWorkMinutesBy(value: Int) {
		var workMinutes = _uiState.value.workMinutes.toInt()
		workMinutes += value
		_uiState.value = _uiState.value.copy(workMinutes = workMinutes.toString()).validate()
	}

	fun increaseWorkSecondsByOne() = alterWorkSecondsBy(1)

	fun decreaseWorkSecondsByOne() = alterWorkSecondsBy(-1)

	private fun alterWorkSecondsBy(value: Int) {
		var workSeconds = _uiState.value.workSeconds.toInt()
		workSeconds += value
		_uiState.value = _uiState.value.copy(workSeconds = workSeconds.toString()).validate()
	}

	fun increaseRestMinutesByOne() = alterRestMinutesBy(1)

	fun decreaseRestMinutesByOne() = alterRestMinutesBy(-1)

	private fun alterRestMinutesBy(value: Int) {
		var restMinutes = _uiState.value.restMinutes.toInt()
		restMinutes += value
		_uiState.value = _uiState.value.copy(restMinutes = restMinutes.toString()).validate()
	}

	fun increaseRestSecondsByOne() = alterRestSecondsBy(1)

	fun decreaseRestSecondsByOne() = alterRestSecondsBy(-1)

	private fun alterRestSecondsBy(value: Int) {
		var restSeconds = _uiState.value.restSeconds.toInt()
		restSeconds += value
		_uiState.value = _uiState.value.copy(restSeconds = restSeconds.toString()).validate()
	}

	fun validateInput() {
		_uiState.value = _uiState.value.validate()
	}
}
