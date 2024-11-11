package com.example.simpleintervaltimer.timer.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.exoplayer.ExoPlayer
import com.example.simpleintervaltimer.R
import com.example.simpleintervaltimer.common.presentation.KeepScreenOn
import com.example.simpleintervaltimer.timer.data.TimeInterval
import com.example.simpleintervaltimer.timer.data.TimerSoundDefinition
import com.example.simpleintervaltimer.ui.theme.Grey2
import com.example.simpleintervaltimer.ui.theme.SimpleintervaltimerTheme

@Composable
fun TimerScreen(
    timeInterval: TimeInterval,
    timerViewModel: TimerViewModel = viewModel(
        factory = TimerViewModelFactory(
            timeInterval,
            ExoPlayer.Builder(LocalContext.current).build(),
            TimerSoundDefinition.fromResourceIds(
                R.raw.beep,
                R.raw.end_interval_beep,
                R.raw.beep,
                R.raw.end_interval_beep,
                R.raw.end_timer_beep
            )
        )
    ),
    onCloseTimer: () -> Unit
) {
    val uiState = timerViewModel.uiState.collectAsState().value
    KeepScreenOn()
    LaunchedEffect(key1 = true) {
        timerViewModel.startTimer()
    }
    BackHandler {
        timerViewModel.showCloseTimerDialog()
    }
    CloseTimerDialog(
        uiState.showCloseTimerDialog,
        onDismissRequest = { timerViewModel.dismissCloseTimerDialog() },
        onConfirmClose = { onCloseTimer() }
    )
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (constRefIconButtonCloseTimer, constRefTextIntervalsLeft, constRefProgressTimer, constRefTextProgressState, constRefButtonPauseResume) = createRefs()
        IconButton(
            modifier = Modifier
                .constrainAs(constRefIconButtonCloseTimer) {
                    start.linkTo(parent.start, margin = 8.dp)
                    top.linkTo(parent.top, margin = 8.dp)
                },
            onClick = { timerViewModel.showCloseTimerDialog() },
            content = {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }
        )
        Text(
            modifier = Modifier
                .constrainAs(constRefTextIntervalsLeft) {
                    bottom.linkTo(constRefProgressTimer.top, margin = 20.dp)
                }
                .fillMaxWidth(),
            text = uiState.getRemainingIntervalsText(),
            style = TextStyle(
                fontSize = 40.sp, color = Color.White, fontWeight = FontWeight.Normal
            ),
            textAlign = TextAlign.Center
        )
        ProgressTimer(
            modifier = Modifier
                .constrainAs(constRefProgressTimer) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            progress = uiState.percentageDone,
            color = uiState.intervalState.toStateColor(),
            timeString = uiState.getRemainingTimeFormatted()
        )
        Text(
            modifier = Modifier
                .constrainAs(constRefTextProgressState) {
                    top.linkTo(constRefProgressTimer.bottom, margin = 20.dp)
                }
                .fillMaxWidth(),
            text = uiState.intervalState.toStateString(),
            style = TextStyle(
                fontSize = 70.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            ), textAlign = TextAlign.Center
        )
        PauseResumeButton(
            modifier = Modifier
                .constrainAs(constRefButtonPauseResume) {
                    top.linkTo(constRefTextProgressState.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth()
                .padding(all = 20.dp),
            visible = uiState.isPauseResumeButtonVisible(),
            buttonText = uiState.getResumeStopButtonText(),
            onClickAction = timerViewModel::pauseOrResumeTimer
        )
    }
}

@Composable
fun CloseTimerDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmClose: () -> Unit
) {
    if (!showDialog) return
    AlertDialog(
        title = { Text("Close Timer") },
        text = { Text("Are you sure you want to close the timer?") },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmClose()
                    onDismissRequest()
                }
            ) {
                Text("Close")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismissRequest() }
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ProgressTimer(
    modifier: Modifier = Modifier,
    progress: Float,
    color: Color,
    timeString: String
) {
    Box(modifier = modifier) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .align(Alignment.Center)
                .size(300.dp),
            color = color,
            strokeWidth = 10.dp,
            trackColor = Grey2
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = timeString,
            style = TextStyle(
                fontSize = 80.sp, fontWeight = FontWeight.SemiBold, color = Grey2
            )
        )
    }
}

@Composable
private fun PauseResumeButton(
    modifier: Modifier = Modifier,
    visible: Boolean,
    buttonText: String,
    onClickAction: () -> Unit
) {
    if (!visible) return
    Button(modifier = modifier, onClick = { onClickAction() }) {
        Text(
            text = buttonText, style = TextStyle(
                fontSize = 70.sp
            ), textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimerPreview() {
    SimpleintervaltimerTheme {
        TimerScreen(
            TimeInterval(5_000, 2_000, 10),
            onCloseTimer = {}
        )
    }
}
