package com.example.simpleintervaltimer.timer.presentation.components.time_interval_input

import android.util.Log
import com.example.simpleintervaltimer.timer.domain.models.TimeInterval
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimeIntervalInputViewModelTest {
	val initialTimeInterval = TimeInterval(30_000, 30_000, 10)

	@Test
	fun timeIntervalInputViewModel_uiState_initialData() {
		val viewModel = TimeIntervalInputViewModel(initialTimeInterval)
		assertEquals("10", viewModel.uiState.value.intervals)
		assertEquals("00", viewModel.uiState.value.workMinutes)
		assertEquals("30", viewModel.uiState.value.workSeconds)
		assertEquals("00", viewModel.uiState.value.restMinutes)
		assertEquals("30", viewModel.uiState.value.restSeconds)
		assertEquals(initialTimeInterval.intervals, viewModel.uiState.value.toTimeInterval().intervals)
		assertEquals(initialTimeInterval.workTime, viewModel.uiState.value.toTimeInterval().workTime)
		assertEquals(initialTimeInterval.restTime, viewModel.uiState.value.toTimeInterval().restTime)
	}

	@Test
	fun timeIntervalInputViewModel_setValues() {
		val viewModel = TimeIntervalInputViewModel(initialTimeInterval)
		viewModel.setIntervalCount("15")
		assertEquals("15", viewModel.uiState.value.intervals)
		assertEquals("00", viewModel.uiState.value.workMinutes)
		assertEquals("30", viewModel.uiState.value.workSeconds)
		assertEquals("00", viewModel.uiState.value.restMinutes)
		assertEquals("30", viewModel.uiState.value.restSeconds)
		viewModel.setWorkIntervalMinutes("1")
		assertEquals("15", viewModel.uiState.value.intervals)
		assertEquals("1", viewModel.uiState.value.workMinutes)
		assertEquals("30", viewModel.uiState.value.workSeconds)
		assertEquals("00", viewModel.uiState.value.restMinutes)
		assertEquals("30", viewModel.uiState.value.restSeconds)
		viewModel.setWorkIntervalSeconds("45")
		viewModel.setRestIntervalMinutes("1")
		viewModel.setRestIntervalSeconds("45")
		assertEquals("15", viewModel.uiState.value.intervals)
		assertEquals("1", viewModel.uiState.value.workMinutes)
		assertEquals("45", viewModel.uiState.value.workSeconds)
		assertEquals("1", viewModel.uiState.value.restMinutes)
		assertEquals("45", viewModel.uiState.value.restSeconds)
	}

	@Test
	fun timeIntervalInputViewModel_setValues_invalidValues() {
		val viewModel = TimeIntervalInputViewModel(initialTimeInterval)
		viewModel.setIntervalCount("1500")
		assertEquals("1500", viewModel.uiState.value.intervals)
		viewModel.setWorkIntervalMinutes("100")
		assertEquals("100", viewModel.uiState.value.workMinutes)
		viewModel.setWorkIntervalSeconds("60")
		assertEquals("60", viewModel.uiState.value.workSeconds)
		viewModel.setRestIntervalMinutes("100")
		assertEquals("100", viewModel.uiState.value.restMinutes)
		viewModel.setRestIntervalSeconds("60")
		assertEquals("60", viewModel.uiState.value.restSeconds)
	}

	@Test
	fun timeIntervalInputViewModel_validateInput_outOfRangeValues() {
		val viewModel = TimeIntervalInputViewModel(initialTimeInterval)
		viewModel.setIntervalCount("1500")
		viewModel.setWorkIntervalMinutes("100")
		viewModel.setWorkIntervalSeconds("60")
		viewModel.setRestIntervalMinutes("100")
		viewModel.setRestIntervalSeconds("60")
		assertEquals("1500", viewModel.uiState.value.intervals)
		assertEquals("100", viewModel.uiState.value.workMinutes)
		assertEquals("60", viewModel.uiState.value.workSeconds)
		assertEquals("100", viewModel.uiState.value.restMinutes)
		assertEquals("60", viewModel.uiState.value.restSeconds)
		viewModel.validateInput()
		assertEquals("1000", viewModel.uiState.value.intervals)
		assertEquals("99", viewModel.uiState.value.workMinutes)
		assertEquals("59", viewModel.uiState.value.workSeconds)
		assertEquals("99", viewModel.uiState.value.restMinutes)
		assertEquals("59", viewModel.uiState.value.restSeconds)
		viewModel.setIntervalCount("-1000")
		viewModel.setWorkIntervalMinutes("-99")
		viewModel.setWorkIntervalSeconds("-59")
		viewModel.setRestIntervalMinutes("-99")
		viewModel.setRestIntervalSeconds("-59")
		viewModel.validateInput()
		assertEquals("1", viewModel.uiState.value.intervals)
		assertEquals("00", viewModel.uiState.value.workMinutes)
		assertEquals("01", viewModel.uiState.value.workSeconds)
		assertEquals("00", viewModel.uiState.value.restMinutes)
		assertEquals("01", viewModel.uiState.value.restSeconds)
	}

	@Test
	fun timeIntervalInputViewModel_invalidateInput_invalidValues() {
		// mock the Android.util.Log.e method, otherwise an exception is thrown
		mockkStatic(Log::class)
		every { Log.e(any(), any()) } returns 0

		val viewModel = TimeIntervalInputViewModel(initialTimeInterval)
		viewModel.setIntervalCount("")
		viewModel.setWorkIntervalMinutes("")
		viewModel.setWorkIntervalSeconds("")
		viewModel.setRestIntervalMinutes("")
		viewModel.setRestIntervalSeconds("")
		viewModel.validateInput()
		assertEquals("1", viewModel.uiState.value.intervals)
		assertEquals("00", viewModel.uiState.value.workMinutes)
		assertEquals("01", viewModel.uiState.value.workSeconds)
		assertEquals("00", viewModel.uiState.value.restMinutes)
		assertEquals("01", viewModel.uiState.value.restSeconds)
	}

	@Test
	fun timeIntervalInputViewModel_intervals_increaseAndDecrease() {
		val viewModel = TimeIntervalInputViewModel(initialTimeInterval)
		assertEquals("10", viewModel.uiState.value.intervals)
		viewModel.increaseIntervalsByOne()
		assertEquals("11", viewModel.uiState.value.intervals)
		viewModel.decreaseIntervalsByOne()
		assertEquals("10", viewModel.uiState.value.intervals)
		viewModel.setIntervalCount("999")
		assertEquals("999", viewModel.uiState.value.intervals)
		viewModel.increaseIntervalsByOne()
		assertEquals("1000", viewModel.uiState.value.intervals)
		viewModel.increaseIntervalsByOne()
		assertEquals("1000", viewModel.uiState.value.intervals)
		viewModel.setIntervalCount("2")
		viewModel.decreaseIntervalsByOne()
		assertEquals("1", viewModel.uiState.value.intervals)
		viewModel.decreaseIntervalsByOne()
		assertEquals("1", viewModel.uiState.value.intervals)
	}

	@Test
	fun timeIntervalInputViewModel_workTime_increaseAndDecrease() {
		val viewModel = TimeIntervalInputViewModel(initialTimeInterval)
		assertEquals("00", viewModel.uiState.value.workMinutes)
		assertEquals("30", viewModel.uiState.value.workSeconds)
		viewModel.increaseWorkMinutesByOne()
		assertEquals("01", viewModel.uiState.value.workMinutes)
		assertEquals("30", viewModel.uiState.value.workSeconds)
		viewModel.decreaseWorkMinutesByOne()
		assertEquals("00", viewModel.uiState.value.workMinutes)
		assertEquals("30", viewModel.uiState.value.workSeconds)
		viewModel.increaseWorkSecondsByOne()
		assertEquals("00", viewModel.uiState.value.workMinutes)
		assertEquals("31", viewModel.uiState.value.workSeconds)
		viewModel.decreaseWorkSecondsByOne()
		assertEquals("00", viewModel.uiState.value.workMinutes)
		assertEquals("30", viewModel.uiState.value.workSeconds)
		viewModel.setWorkIntervalMinutes("99")
		viewModel.setWorkIntervalSeconds("59")
		assertEquals("99", viewModel.uiState.value.workMinutes)
		assertEquals("59", viewModel.uiState.value.workSeconds)
		viewModel.increaseWorkMinutesByOne()
		viewModel.increaseWorkSecondsByOne()
		assertEquals("99", viewModel.uiState.value.workMinutes)
		assertEquals("59", viewModel.uiState.value.workSeconds)
		viewModel.setWorkIntervalMinutes("00")
		viewModel.setWorkIntervalSeconds("01")
		viewModel.decreaseWorkMinutesByOne()
		viewModel.decreaseWorkSecondsByOne()
		assertEquals("00", viewModel.uiState.value.workMinutes)
		assertEquals("01", viewModel.uiState.value.workSeconds)
		viewModel.increaseWorkMinutesByOne()
		viewModel.decreaseWorkSecondsByOne()
		assertEquals("01", viewModel.uiState.value.workMinutes)
		assertEquals("00", viewModel.uiState.value.workSeconds)
		viewModel.decreaseWorkMinutesByOne()
		assertEquals("00", viewModel.uiState.value.workMinutes)
		assertEquals("01", viewModel.uiState.value.workSeconds)
	}

	@Test
	fun timeIntervalInputViewModel_restTime_increaseAndDecrease() {
		val viewModel = TimeIntervalInputViewModel(initialTimeInterval)
		assertEquals("00", viewModel.uiState.value.restMinutes)
		assertEquals("30", viewModel.uiState.value.restSeconds)
		viewModel.increaseRestMinutesByOne()
		assertEquals("01", viewModel.uiState.value.restMinutes)
		assertEquals("30", viewModel.uiState.value.restSeconds)
		viewModel.decreaseRestMinutesByOne()
		assertEquals("00", viewModel.uiState.value.restMinutes)
		assertEquals("30", viewModel.uiState.value.restSeconds)
		viewModel.increaseRestSecondsByOne()
		assertEquals("00", viewModel.uiState.value.restMinutes)
		assertEquals("31", viewModel.uiState.value.restSeconds)
		viewModel.decreaseRestSecondsByOne()
		assertEquals("00", viewModel.uiState.value.restMinutes)
		assertEquals("30", viewModel.uiState.value.restSeconds)
		viewModel.setRestIntervalMinutes("99")
		viewModel.setRestIntervalSeconds("59")
		assertEquals("99", viewModel.uiState.value.restMinutes)
		assertEquals("59", viewModel.uiState.value.restSeconds)
		viewModel.increaseRestMinutesByOne()
		viewModel.increaseRestSecondsByOne()
		assertEquals("99", viewModel.uiState.value.restMinutes)
		assertEquals("59", viewModel.uiState.value.restSeconds)
		viewModel.setRestIntervalMinutes("00")
		viewModel.setRestIntervalSeconds("01")
		viewModel.decreaseRestMinutesByOne()
		viewModel.decreaseRestSecondsByOne()
		assertEquals("00", viewModel.uiState.value.restMinutes)
		assertEquals("01", viewModel.uiState.value.restSeconds)
		viewModel.increaseRestMinutesByOne()
		viewModel.decreaseRestSecondsByOne()
		assertEquals("01", viewModel.uiState.value.restMinutes)
		assertEquals("00", viewModel.uiState.value.restSeconds)
		viewModel.decreaseRestMinutesByOne()
		assertEquals("00", viewModel.uiState.value.restMinutes)
		assertEquals("01", viewModel.uiState.value.restSeconds)
	}
}
