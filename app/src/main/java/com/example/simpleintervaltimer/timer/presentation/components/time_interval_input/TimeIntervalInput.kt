package com.example.simpleintervaltimer.timer.presentation.components.time_interval_input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simpleintervaltimer.R
import com.example.simpleintervaltimer.timer.domain.models.TimeInterval
import com.example.simpleintervaltimer.ui.theme.SimpleintervaltimerTheme

const val WORK_TIME_INPUT_TEST_TAG = "WORK_TIME_INPUT_TEST_TAG"
const val REST_TIME_INPUT_TEST_TAG = "REST_TIME_INPUT_TEST_TAG"
const val MINUTE_INPUT_TEST_TAG = "MINUTE_INPUT_TEST_TAG"
const val SECOND_INPUT_TEST_TAG = "SECOND_INPUT_TEST_TAG"

private val iconSize = 20.dp
private const val iconAlpha = 0.2f

@Composable
fun TimeIntervalInput(
    modifier: Modifier = Modifier,
    initialTimeInterval: TimeInterval,
    onTimeIntervalChanged: (TimeInterval) -> Unit,
    viewModel: TimeIntervalInputViewModel = viewModel(
        factory = TimeIntervalInputViewModelFactory(initialTimeInterval)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    onTimeIntervalChanged(uiState.validate().toTimeInterval())

    Column(
        modifier = modifier.width(400.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlusMinusNumberSelector(
            label = stringResource(R.string.intervals),
            value = uiState.intervals,
            plusButtonContentDescription = stringResource(R.string.increase_intervals),
            minusButtonContentDescription = stringResource(R.string.decrease_intervals),
            onValueChanged = { viewModel.setIntervalCount(it) },
            onPlusButtonClick = { viewModel.increaseIntervalsByOne() },
            onMinusButtonClick = { viewModel.decreaseIntervalsByOne() },
            onFocusLeft = { viewModel.validateInput() },
            onKeyboardActionDone = { viewModel.validateInput() }
        )
        Spacer(Modifier.height(8.dp))
        MinuteSecondInput(
            modifier = Modifier.testTag(WORK_TIME_INPUT_TEST_TAG),
            label = stringResource(R.string.work_time),
            minuteTextValue = uiState.workMinutes,
            secondTextValue = uiState.workSeconds,
            onMinuteTextValueChange = { viewModel.setWorkIntervalMinutes(it) },
            onSecondTextValueChange = { viewModel.setWorkIntervalSeconds(it) },
            onMinuteUpButtonClick = { viewModel.increaseWorkMinutesByOne() },
            onMinuteDownButtonClick = { viewModel.decreaseWorkMinutesByOne() },
            onSecondUpButtonClick = { viewModel.increaseWorkSecondsByOne() },
            onSecondDownButtonClick = { viewModel.decreaseWorkSecondsByOne() },
            onFocusLeft = { viewModel.validateInput() },
            onKeyboardActionDone = { viewModel.validateInput() }
        )
        Spacer(Modifier.height(8.dp))
        MinuteSecondInput(
            modifier = Modifier.testTag(REST_TIME_INPUT_TEST_TAG),
            label = stringResource(R.string.rest_time),
            minuteTextValue = uiState.restMinutes,
            secondTextValue = uiState.restSeconds,
            onMinuteTextValueChange = { viewModel.setRestIntervalMinutes(it) },
            onSecondTextValueChange = { viewModel.setRestIntervalSeconds(it) },
            onMinuteUpButtonClick = { viewModel.increaseRestMinutesByOne() },
            onMinuteDownButtonClick = { viewModel.decreaseRestMinutesByOne() },
            onSecondUpButtonClick = { viewModel.increaseRestSecondsByOne() },
            onSecondDownButtonClick = { viewModel.decreaseRestSecondsByOne() },
            onFocusLeft = { viewModel.validateInput() },
            onKeyboardActionDone = { viewModel.validateInput() }
        )
    }
}

@Composable
private fun PlusMinusNumberSelector(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    plusButtonContentDescription: String? = null,
    minusButtonContentDescription: String? = null,
    onValueChanged: (String) -> Unit,
    onPlusButtonClick: () -> Unit,
    onMinusButtonClick: () -> Unit,
    onFocusLeft: () -> Unit = {},
    onKeyboardActionDone: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        Text(text = label)
        OutlinedCard(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onMinusButtonClick() }) {
                    Icon(
                        modifier = Modifier
                            .alpha(iconAlpha)
                            .size(iconSize),
                        painter = painterResource(R.drawable.ic_remove),
                        contentDescription = minusButtonContentDescription
                    )
                }
                BasicTextField(
                    modifier = Modifier
                        .width(100.dp)
                        .onFocusChanged {
                            if (!it.isFocused) {
                                onFocusLeft()
                            }
                        },
                    value = value,
                    onValueChange = { onValueChanged(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 30.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onKeyboardActionDone()
                        }
                    )
                )
                IconButton(onClick = { onPlusButtonClick() }) {
                    Icon(
                        modifier = Modifier
                            .alpha(iconAlpha)
                            .size(iconSize),
                        imageVector = Icons.Default.Add,
                        contentDescription = plusButtonContentDescription
                    )
                }
            }
        }
    }
}

@Composable
private fun MinuteSecondInput(
    modifier: Modifier = Modifier,
    label: String,
    minuteTextValue: String,
    secondTextValue: String,
    onMinuteTextValueChange: (String) -> Unit,
    onSecondTextValueChange: (String) -> Unit,
    onMinuteUpButtonClick: () -> Unit,
    onMinuteDownButtonClick: () -> Unit,
    onSecondUpButtonClick: () -> Unit,
    onSecondDownButtonClick: () -> Unit,
    onFocusLeft: () -> Unit = {},
    onKeyboardActionDone: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        Text(text = label)
        OutlinedCard(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                UpDownNumberSelector(
                    modifier = Modifier.testTag(MINUTE_INPUT_TEST_TAG),
                    value = minuteTextValue,
                    upButtonContentDescription = stringResource(R.string.increase_minutes),
                    downButtonContentDescription = stringResource(R.string.decrease_minutes),
                    onValueChanged = onMinuteTextValueChange,
                    onUpButtonClick = onMinuteUpButtonClick,
                    onDownButtonClick = onMinuteDownButtonClick,
                    onFocusLeft = onFocusLeft,
                    onKeyboardActionDone = onKeyboardActionDone
                )
                Text(
                    modifier = Modifier.clearAndSetSemantics { },
                    text = ":",
                    fontSize = 30.sp
                )
                UpDownNumberSelector(
                    modifier = Modifier.testTag(SECOND_INPUT_TEST_TAG),
                    value = secondTextValue,
                    upButtonContentDescription = stringResource(R.string.increase_seconds),
                    downButtonContentDescription = stringResource(R.string.decrease_seconds),
                    onValueChanged = onSecondTextValueChange,
                    onUpButtonClick = onSecondUpButtonClick,
                    onDownButtonClick = onSecondDownButtonClick,
                    onFocusLeft = onFocusLeft,
                    onKeyboardActionDone = onKeyboardActionDone
                )
            }
        }
    }
}

@Composable
private fun UpDownNumberSelector(
    modifier: Modifier = Modifier,
    value: String,
    upButtonContentDescription: String? = null,
    downButtonContentDescription: String? = null,
    onValueChanged: (String) -> Unit,
    onUpButtonClick: () -> Unit,
    onDownButtonClick: () -> Unit,
    onFocusLeft: () -> Unit = {},
    onKeyboardActionDone: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onUpButtonClick) {
            Icon(
                modifier = Modifier
                    .alpha(iconAlpha)
                    .size(iconSize),
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = upButtonContentDescription
            )
        }
        BasicTextField(
            modifier = Modifier
                .width(70.dp)
                .onFocusChanged {
                    if (!it.isFocused) {
                        onFocusLeft()
                    }
                },
            value = value,
            onValueChange = onValueChanged,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onKeyboardActionDone()
                }
            )
        )
        IconButton(onClick = onDownButtonClick) {
            Icon(
                modifier = Modifier
                    .alpha(iconAlpha)
                    .size(iconSize),
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = downButtonContentDescription
            )
        }
    }
}

@PreviewFontScale
@PreviewScreenSizes
@Preview
@Composable
fun TimeIntervalInputPreview() {
    SimpleintervaltimerTheme {
        TimeIntervalInput(
            initialTimeInterval = TimeInterval(30_000, 30_000, 10),
            onTimeIntervalChanged = {}
        )
    }
}
