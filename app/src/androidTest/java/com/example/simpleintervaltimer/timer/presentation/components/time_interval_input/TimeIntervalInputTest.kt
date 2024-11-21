package com.example.simpleintervaltimer.timer.presentation.components.time_interval_input

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.platform.app.InstrumentationRegistry
import com.example.simpleintervaltimer.R
import com.example.simpleintervaltimer.timer.domain.models.TimeInterval
import com.example.simpleintervaltimer.ui.theme.SimpleintervaltimerTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TimeIntervalInputTest {
    val context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    val rule = createComposeRule()

    @Before
    fun setUp() {
        // start the composable for every test
        rule.setContent {
            SimpleintervaltimerTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TimeIntervalInput(
                        initialTimeInterval = TimeInterval(30_000, 30_000, 10),
                        onTimeIntervalChanged = {}
                    )
                }
            }
        }
    }

    private val intervalsLabelText = hasText(context.getString(R.string.intervals))
    private val decreaseIntervalsButton = hasContentDescription(context.getString(R.string.decrease_intervals)) and hasClickAction()
    private val increaseIntervalsButton = hasContentDescription(context.getString(R.string.increase_intervals)) and hasClickAction()
    private val intervalsTextField = hasAnySibling(decreaseIntervalsButton) and
            hasAnySibling(increaseIntervalsButton) and
            hasSetTextAction()

    @Test
    fun testIntervalInput() {
        rule.onNode(intervalsLabelText).assertExists()
        rule.onNode(decreaseIntervalsButton).assertExists()
        rule.onNode(increaseIntervalsButton).assertExists()
        rule.onNode(intervalsTextField).assertExists().assertTextEquals("10")

        rule.onNode(decreaseIntervalsButton).performClick()
        rule.onNode(intervalsTextField).assertTextEquals("9")
        rule.onNode(increaseIntervalsButton).performClick().performClick()
        rule.onNode(intervalsTextField).assertTextEquals("11")

        rule.onNode(intervalsTextField).performTextReplacement("2")
        rule.onNode(intervalsTextField).assertTextEquals("2")
        rule.onNode(decreaseIntervalsButton).performClick()
        rule.onNode(intervalsTextField).assertTextEquals("1")
        rule.onNode(decreaseIntervalsButton).performClick()
        rule.onNode(intervalsTextField).assertTextEquals("1")

        rule.onNode(intervalsTextField).performTextReplacement("999")
        rule.onNode(intervalsTextField).assertTextEquals("999")
        rule.onNode(increaseIntervalsButton).performClick()
        rule.onNode(intervalsTextField).assertTextEquals("1000")
        rule.onNode(increaseIntervalsButton).performClick()
        rule.onNode(intervalsTextField).assertTextEquals("1000")
    }

    private val workTimeLabelText = hasText(context.getString(R.string.work_time))

    private val _workMinutesInputNode = hasTestTag(MINUTE_INPUT_TEST_TAG) and hasParent(hasParent(hasTestTag(WORK_TIME_INPUT_TEST_TAG)))
    private val increaseWorkMinutesButton = hasContentDescription(context.getString(R.string.increase_minutes)) and hasClickAction() and
            hasParent(_workMinutesInputNode)
    private val decreaseWorkMinutesButton = hasContentDescription(context.getString(R.string.decrease_minutes)) and hasClickAction() and
            hasParent(_workMinutesInputNode)
    private val workMinutesTextField = hasSetTextAction() and hasParent(_workMinutesInputNode)

    private val _workSecondsInputNode = hasTestTag(SECOND_INPUT_TEST_TAG) and hasParent(hasParent(hasTestTag(WORK_TIME_INPUT_TEST_TAG)))
    private val increaseWorkSecondsButton = hasContentDescription(context.getString(R.string.increase_seconds)) and hasClickAction() and
            hasParent(_workSecondsInputNode)
    private val decreaseWorkSecondsButton = hasContentDescription(context.getString(R.string.decrease_seconds)) and hasClickAction() and
            hasParent(_workSecondsInputNode)
    private val workSecondsTextField = hasSetTextAction() and hasParent(_workSecondsInputNode)

    @Test
    fun testWorkTimeInput() {
        rule.onNode(workTimeLabelText).assertExists()
        rule.onNode(increaseWorkMinutesButton).assertExists()
        rule.onNode(decreaseWorkMinutesButton).assertExists()
        rule.onNode(workMinutesTextField)
            .assertExists()
            .assertTextEquals("00")
        rule.onNode(increaseWorkSecondsButton).assertExists()
        rule.onNode(decreaseWorkSecondsButton).assertExists()
        rule.onNode(workSecondsTextField)
            .assertExists()
            .assertTextEquals("30")

        rule.onNode(increaseWorkMinutesButton).performClick()
        rule.onNode(workMinutesTextField).assertTextEquals("01")
        rule.onNode(decreaseWorkMinutesButton).performClick().performClick()
        rule.onNode(workMinutesTextField).assertTextEquals("00")
        rule.onNode(increaseWorkSecondsButton).performClick()
        rule.onNode(workSecondsTextField).assertTextEquals("31")
        rule.onNode(decreaseWorkSecondsButton).performClick()
        rule.onNode(workSecondsTextField).assertTextEquals("30")

        rule.onNode(workMinutesTextField).performTextReplacement("98")
        rule.onNode(workMinutesTextField).assertTextEquals("98")
        rule.onNode(increaseWorkMinutesButton).performClick()
        rule.onNode(workMinutesTextField).assertTextEquals("99")
        rule.onNode(increaseWorkMinutesButton).performClick()
        rule.onNode(workMinutesTextField).assertTextEquals("99")
        rule.onNode(workMinutesTextField).performTextReplacement("1")
        rule.onNode(workMinutesTextField).assertTextEquals("1")
        rule.onNode(decreaseWorkMinutesButton).performClick()
        rule.onNode(workMinutesTextField).assertTextEquals("00")
        rule.onNode(decreaseWorkMinutesButton).performClick()
        rule.onNode(workMinutesTextField).assertTextEquals("00")

        rule.onNode(workSecondsTextField).performTextReplacement("58")
        rule.onNode(workSecondsTextField).assertTextEquals("58")
        rule.onNode(increaseWorkSecondsButton).performClick()
        rule.onNode(workSecondsTextField).assertTextEquals("59")
        rule.onNode(increaseWorkSecondsButton).performClick()
        rule.onNode(workSecondsTextField).assertTextEquals("59")
        rule.onNode(workSecondsTextField).performTextReplacement("2")
        rule.onNode(workSecondsTextField).assertTextEquals("2")
        rule.onNode(decreaseWorkSecondsButton).performClick()
        rule.onNode(workSecondsTextField).assertTextEquals("01")
        rule.onNode(decreaseWorkSecondsButton).performClick()
        rule.onNode(workSecondsTextField).assertTextEquals("01")

        rule.onNode(increaseWorkMinutesButton).performClick()
        rule.onNode(decreaseWorkSecondsButton).performClick()
        rule.onNode(workMinutesTextField).assertTextEquals("01")
        rule.onNode(workSecondsTextField).assertTextEquals("00")
        rule.onNode(decreaseWorkMinutesButton).performClick()
        rule.onNode(workMinutesTextField).assertTextEquals("00")
        rule.onNode(workSecondsTextField).assertTextEquals("01")
    }

    private val restTimeLabelText = hasText(context.getString(R.string.rest_time))

    private val _restMinutesInputNode = hasTestTag(MINUTE_INPUT_TEST_TAG) and hasParent(hasParent(hasTestTag(REST_TIME_INPUT_TEST_TAG)))
    private val increaseRestMinutesButton = hasContentDescription(context.getString(R.string.increase_minutes)) and hasClickAction() and
            hasParent(_restMinutesInputNode)
    private val decreaseRestMinutesButton = hasContentDescription(context.getString(R.string.decrease_minutes)) and hasClickAction() and
            hasParent(_restMinutesInputNode)
    private val restMinutesTextField = hasSetTextAction() and hasParent(_restMinutesInputNode)

    private val _restSecondsInputNode = hasTestTag(SECOND_INPUT_TEST_TAG) and hasParent(hasParent(hasTestTag(REST_TIME_INPUT_TEST_TAG)))
    private val increaseRestSecondsButton = hasContentDescription(context.getString(R.string.increase_seconds)) and hasClickAction() and
            hasParent(_restSecondsInputNode)
    private val decreaseRestSecondsButton = hasContentDescription(context.getString(R.string.decrease_seconds)) and hasClickAction() and
            hasParent(_restSecondsInputNode)
    private val restSecondsTextField = hasSetTextAction() and hasParent(_restSecondsInputNode)

    @Test
    fun testRestTimeInput() {
        rule.onNode(restTimeLabelText).assertExists()
        rule.onNode(increaseRestMinutesButton).assertExists()
        rule.onNode(decreaseRestMinutesButton).assertExists()
        rule.onNode(restMinutesTextField)
            .assertExists()
            .assertTextEquals("00")
        rule.onNode(increaseRestSecondsButton).assertExists()
        rule.onNode(decreaseRestSecondsButton).assertExists()
        rule.onNode(restSecondsTextField)
            .assertExists()
            .assertTextEquals("30")

        rule.onNode(increaseRestMinutesButton).performClick()
        rule.onNode(restMinutesTextField).assertTextEquals("01")
        rule.onNode(decreaseRestMinutesButton).performClick().performClick()
        rule.onNode(restMinutesTextField).assertTextEquals("00")
        rule.onNode(increaseRestSecondsButton).performClick()
        rule.onNode(restSecondsTextField).assertTextEquals("31")
        rule.onNode(decreaseRestSecondsButton).performClick()
        rule.onNode(restSecondsTextField).assertTextEquals("30")

        rule.onNode(restMinutesTextField).performTextReplacement("98")
        rule.onNode(restMinutesTextField).assertTextEquals("98")
        rule.onNode(increaseRestMinutesButton).performClick()
        rule.onNode(restMinutesTextField).assertTextEquals("99")
        rule.onNode(increaseRestMinutesButton).performClick()
        rule.onNode(restMinutesTextField).assertTextEquals("99")
        rule.onNode(restMinutesTextField).performTextReplacement("1")
        rule.onNode(restMinutesTextField).assertTextEquals("1")
        rule.onNode(decreaseRestMinutesButton).performClick()
        rule.onNode(restMinutesTextField).assertTextEquals("00")
        rule.onNode(decreaseRestMinutesButton).performClick()
        rule.onNode(restMinutesTextField).assertTextEquals("00")

        rule.onNode(restSecondsTextField).performTextReplacement("58")
        rule.onNode(restSecondsTextField).assertTextEquals("58")
        rule.onNode(increaseRestSecondsButton).performClick()
        rule.onNode(restSecondsTextField).assertTextEquals("59")
        rule.onNode(increaseRestSecondsButton).performClick()
        rule.onNode(restSecondsTextField).assertTextEquals("59")
        rule.onNode(restSecondsTextField).performTextReplacement("2")
        rule.onNode(restSecondsTextField).assertTextEquals("2")
        rule.onNode(decreaseRestSecondsButton).performClick()
        rule.onNode(restSecondsTextField).assertTextEquals("01")
        rule.onNode(decreaseRestSecondsButton).performClick()
        rule.onNode(restSecondsTextField).assertTextEquals("01")

        rule.onNode(increaseRestMinutesButton).performClick()
        rule.onNode(decreaseRestSecondsButton).performClick()
        rule.onNode(restMinutesTextField).assertTextEquals("01")
        rule.onNode(restSecondsTextField).assertTextEquals("00")
        rule.onNode(decreaseRestMinutesButton).performClick()
        rule.onNode(restMinutesTextField).assertTextEquals("00")
        rule.onNode(restSecondsTextField).assertTextEquals("01")
    }
}
