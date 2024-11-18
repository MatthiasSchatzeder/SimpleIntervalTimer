package com.example.simpleintervaltimer.timer.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simpleintervaltimer.timer.data.data_sources.TimerSettingsLocalDataSource
import com.example.simpleintervaltimer.timer.data.datastore.timerSettingsDataStore
import com.example.simpleintervaltimer.timer.data.db.RealmProvider
import com.example.simpleintervaltimer.timer.data.repositories.TimerSettingsRepository
import com.example.simpleintervaltimer.timer.domain.models.TimeInterval
import com.example.simpleintervaltimer.timer.presentation.components.time_interval_input.TimeIntervalInput
import com.example.simpleintervaltimer.ui.theme.SimpleintervaltimerTheme
import kotlinx.coroutines.Dispatchers

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onStartTimer: (timeInterval: TimeInterval) -> Unit
) {
    DisposableEffect(Unit) {
        onDispose {
            RealmProvider.closeRealm()
        }
    }
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
    InputTextDialog(
        showDialog = uiState.showNameInput,
        onDismissRequest = { quickStartViewModel.dismissNameInput() },
        onConfirm = { name -> quickStartViewModel.saveInterval(name) }
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TimeIntervalInput(
            initialTimeInterval = uiState.timeInterval,
            onTimeIntervalChanged = {
                quickStartViewModel.setInterval(it)
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            modifier = Modifier
                .align(Alignment.End),
            onClick = { quickStartViewModel.showNameInput() }
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Save")
        }
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
fun InputTextDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    if (!showDialog) return
    var text by remember { mutableStateOf("") }
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onDismissRequest() },
        title = { Text("Enter Name") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Name") }
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text) }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismissRequest() }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    SimpleintervaltimerTheme {
        HomeScreen(onStartTimer = {})
    }
}
