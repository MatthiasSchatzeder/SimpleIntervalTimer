package com.example.simpleintervaltimer.timer.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simpleintervaltimer.common.presentation.KeepScreenOn
import com.example.simpleintervaltimer.timer.data.TimeInterval
import com.example.simpleintervaltimer.ui.theme.Grey2
import com.example.simpleintervaltimer.ui.theme.SimpleintervaltimerTheme

@Composable
fun TimerScreen(timeInterval: TimeInterval, timerViewModel: TimerViewModel = viewModel()) {
    val uiState = timerViewModel.uiState.collectAsState().value
    KeepScreenOn()
    DisposableEffect(key1 = true) {
        timerViewModel.startTimer(timeInterval)
        onDispose {
            timerViewModel.stopTimer()
        }
    }
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (constRefTextIntervalsLeft, constRefProgressTimer, constRefTextProgressState, constRefButtonPauseResume) = createRefs()
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
        TimerScreen(TimeInterval(5_000, 2_000, 10))
    }
}
