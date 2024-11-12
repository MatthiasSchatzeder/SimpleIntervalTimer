package com.example.simpleintervaltimer.timer.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simpleintervaltimer.timer.data.data_sources.TimerSettingsLocalDataSource
import com.example.simpleintervaltimer.timer.data.datastore.timerSettingsDataStore
import com.example.simpleintervaltimer.timer.data.repositories.TimerSettingsRepository
import com.example.simpleintervaltimer.timer.domain.models.TimeInterval
import com.example.simpleintervaltimer.ui.theme.SimpleintervaltimerTheme
import kotlinx.coroutines.Dispatchers

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onStartTimer: (timeInterval: TimeInterval) -> Unit
) {
    QuickStartTimer(
        modifier = modifier,
        onStartTimer = onStartTimer
    )
}

@Composable
fun QuickStartTimer(
    modifier: Modifier = Modifier,
    onStartTimer: (timeInterval: TimeInterval) -> Unit,
    quickStartViewModel: QuickStartViewModel = viewModel(
        factory = QuickStartViewModelFactory(
            TimerSettingsRepository(
                TimerSettingsLocalDataSource(
                    LocalContext.current.timerSettingsDataStore,
                    Dispatchers.Default
                )
            )
        )
    )
) {
    val uiState by quickStartViewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) {
        return
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = uiState.intervalCount,
            onValueChange = { quickStartViewModel.setIntervalCount(it) },
            label = { Text("Number of Intervals") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.onFocusChanged {
                if (!it.isFocused) {
                    quickStartViewModel.validateInput()
                }
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    quickStartViewModel.validateInput()
                }
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        MinuteSecondInput(modifier = modifier,
            minuteTextLabel = "Work Time (Minutes)",
            secondTextLabel = "Work Time (Seconds)",
            minuteTextValue = uiState.workIntervalMinutes.toString(),
            secondTextValue = uiState.workIntervalSeconds.toString(),
            onMinuteTextValueChange = { quickStartViewModel.setWorkIntervalMinutes(it) },
            onSecondTextValueChange = { quickStartViewModel.setWorkIntervalSeconds(it) },
            validateInput = { quickStartViewModel.validateInput() }
        )
        Spacer(modifier = Modifier.height(8.dp))
        MinuteSecondInput(modifier = modifier,
            minuteTextLabel = "Rest Time (Minutes)",
            secondTextLabel = "Rest Time (Seconds)",
            minuteTextValue = uiState.restIntervalMinutes.toString(),
            secondTextValue = uiState.restIntervalSeconds.toString(),
            onMinuteTextValueChange = { quickStartViewModel.setRestIntervalMinutes(it) },
            onSecondTextValueChange = { quickStartViewModel.setRestIntervalSeconds(it) },
            validateInput = { quickStartViewModel.validateInput() }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                quickStartViewModel.startTimer(onStartTimer)
            }
        ) {
            Text(
                text = "Start Timer",
                style = TextStyle(
                    fontSize = 50.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MinuteSecondInput(
    modifier: Modifier = Modifier,
    minuteTextLabel: String,
    secondTextLabel: String,
    minuteTextValue: String,
    secondTextValue: String,
    onMinuteTextValueChange: (String) -> Unit,
    onSecondTextValueChange: (String) -> Unit,
    validateInput: () -> Unit = {}
) {
    Row(modifier = modifier) {
        OutlinedTextField(
            value = minuteTextValue,
            onValueChange = { onMinuteTextValueChange(it) },
            label = { Text(minuteTextLabel) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged {
                    if (!it.isFocused) {
                        validateInput()
                    }
                },
            keyboardActions = KeyboardActions(
                onDone = {
                    validateInput()
                }
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = secondTextValue,
            onValueChange = { onSecondTextValueChange(it) },
            label = { Text(secondTextLabel) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged {
                    if (!it.isFocused) {
                        validateInput()
                    }
                },
            keyboardActions = KeyboardActions(
                onDone = {
                    validateInput()
                }
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    SimpleintervaltimerTheme {
        HomeScreen(onStartTimer = {})
    }
}
