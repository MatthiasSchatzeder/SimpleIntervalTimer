package com.example.simpleintervaltimer.timer.presentation.timer

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simpleintervaltimer.R
import com.example.simpleintervaltimer.common.presentation.KeepScreenOn
import com.example.simpleintervaltimer.timer.di.ExoPlayerProvider
import com.example.simpleintervaltimer.timer.domain.models.TimeInterval
import com.example.simpleintervaltimer.timer.domain.models.TimerSoundDefinition
import com.example.simpleintervaltimer.timer.presentation.components.SimpleConfirmationDialog
import com.example.simpleintervaltimer.timer.presentation.timer.TimerViewModel.IntervalState.*
import com.example.simpleintervaltimer.ui.theme.SimpleintervaltimerTheme

@Composable
fun TimerScreen(
    timeInterval: TimeInterval,
    timerViewModel: TimerViewModel = viewModel(
        factory = TimerViewModelFactory(
            timeInterval,
            ExoPlayerProvider.getPlayer(LocalContext.current),
            TimerSoundDefinition.fromResourceIds(
                R.raw.beep,
                R.raw.end_interval_beep,
                R.raw.beep,
                R.raw.end_interval_beep,
                R.raw.end_timer_beep
            )
        )
    ),
    onEndTimer: () -> Unit
) {
    val uiState by timerViewModel.uiState.collectAsState()
    KeepScreenOn()
    BackHandler {
        timerViewModel.requestEndTimer(onEndTimer = onEndTimer)
    }
    SimpleConfirmationDialog(
        showDialog = uiState.showCloseTimerDialog,
        title = stringResource(R.string.end_timer),
        text = stringResource(R.string.end_timer_message),
        confirmButtonText = stringResource(R.string.end),
        dismissButtonText = stringResource(R.string.cancel),
        onConfirm = { timerViewModel.requestEndTimer(forceEnd = true, onEndTimer = onEndTimer) },
        onDismissRequest = { timerViewModel.dismissCloseTimerDialog() }
    )
    Timer(
        remainingIntervals = uiState.getRemainingIntervalsText(LocalContext.current),
        percentageDone = uiState.percentageDone,
        stateColor = uiState.intervalState.toStateColor(),
        time = uiState.getRemainingTimeFormatted(),
        stateName = stringResource(uiState.intervalState.getStateStringRes()),
        isPauseResumeButtonVisible = uiState.isPauseResumeButtonVisible(),
        pauseResumeButtonText = getPauseResumeButtonText(uiState.isTimerRunning, LocalContext.current),
        onPauseResumeButtonClick = timerViewModel::pauseOrResumeTimer,
        onEndTimerButtonClick = { timerViewModel.requestEndTimer(onEndTimer = onEndTimer) }
    )
}

private fun TimerViewModel.TimerUiState.getRemainingIntervalsText(context: Context): String {
    if (remainingIntervals == 1) return context.getString(R.string.last_interval)
    if (remainingIntervals <= 0) return ""
    return remainingIntervals.toString()
}

private fun TimerViewModel.IntervalState.getStateStringRes(): Int = when (this) {
    INIT -> R.string.prepare
    WORK -> R.string.work
    REST -> R.string.rest
    DONE -> R.string.done
}

private fun TimerViewModel.IntervalState.toStateColor() = when (this) {
    INIT -> Color.Yellow
    WORK -> Color.Green
    REST -> Color.Blue
    DONE -> Color.Cyan
}

private fun getPauseResumeButtonText(isTimerRunning: Boolean, context: Context): String {
    return if (isTimerRunning) context.getString(R.string.stop) else context.getString(R.string.resume)
}

@Composable
private fun Timer(
    modifier: Modifier = Modifier,
    remainingIntervals: String,
    percentageDone: Float,
    stateColor: Color,
    time: String,
    stateName: String,
    isPauseResumeButtonVisible: Boolean,
    pauseResumeButtonText: String,
    onPauseResumeButtonClick: () -> Unit,
    onEndTimerButtonClick: () -> Unit
) {
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (constRefIconButtonCloseTimer, constRefTextIntervalsLeft, constRefProgressTimer, constRefTextProgressState, constRefButtonPauseResume) = createRefs()
        IconButton(
            modifier = Modifier
                .constrainAs(constRefIconButtonCloseTimer) {
                    start.linkTo(parent.start, margin = 8.dp)
                    top.linkTo(parent.top, margin = 8.dp)
                },
            onClick = onEndTimerButtonClick,
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
            text = remainingIntervals,
            style = TextStyle(
                fontSize = 40.sp,
                fontWeight = FontWeight.Normal
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
            progress = percentageDone,
            color = stateColor,
            timeString = time
        )
        Text(
            modifier = Modifier
                .constrainAs(constRefTextProgressState) {
                    top.linkTo(constRefProgressTimer.bottom, margin = 20.dp)
                }
                .fillMaxWidth(),
            text = stateName,
            style = TextStyle(
                fontSize = 70.sp,
                fontWeight = FontWeight.Bold,
            ),
            textAlign = TextAlign.Center
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
            visible = isPauseResumeButtonVisible,
            buttonText = pauseResumeButtonText,
            onClickAction = onPauseResumeButtonClick
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
            strokeWidth = 10.dp
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = timeString,
            style = TextStyle(
                fontSize = 80.sp,
                fontWeight = FontWeight.SemiBold
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
    Button(
        modifier = modifier,
        onClick = onClickAction
    ) {
        Text(
            text = buttonText,
            style = TextStyle(
                fontSize = 70.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

@PreviewFontScale
@PreviewScreenSizes
@Preview
@Composable
fun TimerPreview() {
    SimpleintervaltimerTheme {
        Timer(
            remainingIntervals = "10",
            percentageDone = 0.5f,
            stateColor = Color.Green,
            time = "15,5",
            stateName = "Work",
            isPauseResumeButtonVisible = true,
            pauseResumeButtonText = "Pause",
            onPauseResumeButtonClick = {},
            onEndTimerButtonClick = {}
        )
    }
}
