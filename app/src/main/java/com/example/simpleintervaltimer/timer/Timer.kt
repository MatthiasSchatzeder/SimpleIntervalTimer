package com.example.simpleintervaltimer.timer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.simpleintervaltimer.timer.data.TimeInterval
import com.example.simpleintervaltimer.ui.theme.Grey2
import com.example.simpleintervaltimer.ui.theme.SimpleintervaltimerTheme

@Composable
fun Timer(timeInterval: TimeInterval, timerViewModel: TimerViewModel = viewModel()) {
    timerViewModel.formattedTime.observeAsState().value
    timerViewModel.timeProgress.observeAsState().value
    timerViewModel.remainingIntervals.observeAsState().value
    timerViewModel.progressColor.observeAsState().value
    timerViewModel.progressState.observeAsState().value
    timerViewModel.startStopButtonText.observeAsState().value
    timerViewModel.pauseResumeButtonVisible.observeAsState().value
    DisposableEffect(key1 = true) {
        timerViewModel.startTimer(timeInterval)
        onDispose {
            timerViewModel.stopTimer()
        }
    }
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (textIntervalsLeft, progressTimer, progressState, playPauseButton) = createRefs()
        Text(
            modifier = Modifier
                .constrainAs(textIntervalsLeft) {
                    bottom.linkTo(progressTimer.top, margin = 20.dp)
                }
                .fillMaxWidth(),
            text = "${timerViewModel.remainingIntervals.value}",
            style = TextStyle(
                fontSize = 40.sp, color = Color.White, fontWeight = FontWeight.Normal
            ),
            textAlign = TextAlign.Center
        )
        ProgressTimer(
            modifier = Modifier
                .constrainAs(progressTimer) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            timerViewModel = timerViewModel
        )
        Text(
            modifier = Modifier
                .constrainAs(progressState) {
                    top.linkTo(progressTimer.bottom, margin = 20.dp)
                }
                .fillMaxWidth(), text = "${timerViewModel.progressState.value}", style = TextStyle(
                fontSize = 70.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            ), textAlign = TextAlign.Center
        )
        PauseResumeButton(
            modifier = Modifier
                .constrainAs(playPauseButton) {
                    top.linkTo(progressState.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth()
                .padding(all = 20.dp),
            timerViewModel = timerViewModel
        )
    }
}

@Composable
private fun ProgressTimer(modifier: Modifier = Modifier, timerViewModel: TimerViewModel) {
    Box(modifier = modifier) {
        CircularProgressIndicator(
            progress = { timerViewModel.timeProgress.value ?: 0f },
            modifier = Modifier
                .align(Alignment.Center)
                .size(300.dp),
            color = timerViewModel.progressColor.value ?: ProgressIndicatorDefaults.circularColor,
            strokeWidth = 10.dp,
            trackColor = Grey2
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "${timerViewModel.formattedTime.value}",
            style = TextStyle(
                fontSize = 80.sp, fontWeight = FontWeight.SemiBold, color = Grey2
            )
        )
    }
}

@Composable
private fun PauseResumeButton(modifier: Modifier = Modifier, timerViewModel: TimerViewModel) {
    if (timerViewModel.pauseResumeButtonVisible.value == false) return
    Button(modifier = modifier, onClick = { timerViewModel.pauseOrResumeTimer() }) {
        Text(
            text = "${timerViewModel.startStopButtonText.value}", style = TextStyle(
                fontSize = 70.sp
            ), textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimerPreview() {
    SimpleintervaltimerTheme {
        Timer(TimeInterval(5_000, 2_000, 10))
    }
}