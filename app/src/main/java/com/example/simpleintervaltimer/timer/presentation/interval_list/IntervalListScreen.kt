package com.example.simpleintervaltimer.timer.presentation.interval_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simpleintervaltimer.R
import com.example.simpleintervaltimer.timer.data.db.realm_objects.StoredTimeInterval
import com.example.simpleintervaltimer.timer.domain.models.TimeInterval
import com.example.simpleintervaltimer.timer.presentation.components.SimpleConfirmationDialog
import com.example.simpleintervaltimer.ui.theme.SimpleintervaltimerTheme

@Composable
fun IntervalListScreen(
    modifier: Modifier = Modifier,
    intervalListViewModel: IntervalListViewModel = viewModel(
        factory = IntervalListViewModelFactory()
    ),
    onStartTimer: (timeInterval: TimeInterval) -> Unit,
    onEditTimeInterval: (storedTimeIntervalIdHexString: String) -> Unit
) {
    val uiState by intervalListViewModel.uiState.collectAsStateWithLifecycle()
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.width(64.dp))
            return
        }
        if (uiState.storedTimeIntervals.isEmpty()) {
            Text(text = "Nothing saved yet")
            return
        }
    }
    SimpleConfirmationDialog(
        showDialog = uiState.storedTimeIntervalToDelete != null,
        title = stringResource(R.string.delete),
        text = stringResource(
            R.string.delete_interval_message_with_placeholder,
            uiState.storedTimeIntervalToDelete?.name ?: stringResource(R.string.null_string)
        ),
        confirmButtonText = stringResource(R.string.delete),
        dismissButtonText = stringResource(R.string.cancel),
        onConfirm = {
            uiState.storedTimeIntervalToDelete?.let {
                intervalListViewModel.deleteStoredTimeInterval(it)
            }
        },
        onDismissRequest = { intervalListViewModel.setStoredTimeIntervalToDelete(null) }
    )
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            items = uiState.storedTimeIntervals,
            key = { it._id.hashCode() }
        ) { storedInterval ->
            StoredTimeIntervalCard(
                modifier = Modifier.animateItem(),
                storedTimeInterval = storedInterval,
                onStartTimer = onStartTimer,
                onDeleteAction = { intervalListViewModel.setStoredTimeIntervalToDelete(storedInterval) },
                onEditAction = { onEditTimeInterval(storedInterval._id.toHexString()) }
            )
        }
    }
}

@Composable
private fun StoredTimeIntervalCard(
    modifier: Modifier = Modifier,
    storedTimeInterval: StoredTimeInterval,
    onStartTimer: (timeInterval: TimeInterval) -> Unit,
    onDeleteAction: () -> Unit,
    onEditAction: () -> Unit
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                fontSize = 24.sp,
                text = storedTimeInterval.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            val timeInterval = TimeInterval(storedTimeInterval.workTime, storedTimeInterval.restTime, storedTimeInterval.intervals)
            HorizontalNameValuePair(
                name = stringResource(R.string.intervals),
                value = timeInterval.intervals.toString()
            )
            HorizontalDivider()
            HorizontalNameValuePair(
                name = stringResource(R.string.work_time),
                value = timeInterval.getDisplayWorkTime()
            )
            HorizontalDivider()
            HorizontalNameValuePair(
                name = stringResource(R.string.rest_time),
                value = timeInterval.getDisplayRestTime()
            )
            Spacer(modifier = Modifier.height(16.dp))
            CardActionButtons(
                timeInterval = timeInterval,
                onStartTimer = onStartTimer,
                onDeleteAction = onDeleteAction,
                onEditAction = onEditAction
            )
        }
    }
}

@Composable
private fun CardActionButtons(
    modifier: Modifier = Modifier,
    timeInterval: TimeInterval,
    onStartTimer: (TimeInterval) -> Unit,
    onDeleteAction: () -> Unit,
    onEditAction: () -> Unit
) {
    Row(modifier = modifier) {
        IconButton(
            onClick = onDeleteAction
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onEditAction
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null
            )
        }
        Spacer(
            modifier = Modifier
                .width(8.dp)
                .weight(1f)
        )
        OutlinedButton(
            onClick = { onStartTimer(timeInterval) }
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(R.string.start))
        }
    }
}

@Composable
private fun HorizontalNameValuePair(
    modifier: Modifier = Modifier,
    name: String,
    value: String
) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier
                .weight(1f)
                .alpha(0.7f),
            text = name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier
                .weight(1f),
            text = value,
            maxLines = 1,
            textAlign = TextAlign.End
        )
    }
}

@PreviewFontScale
@PreviewScreenSizes
@Preview
@Composable
fun StoredTimeIntervalCardPreview() {
    SimpleintervaltimerTheme {
        val storedTimeInterval = StoredTimeInterval().apply {
            name = "My Time Interval"
            workTime = 10000L
            restTime = 5000L
            intervals = 10
        }
        StoredTimeIntervalCard(
            modifier = Modifier.fillMaxWidth(),
            storedTimeInterval = storedTimeInterval,
            onStartTimer = {},
            onDeleteAction = {},
            onEditAction = {}
        )
    }
}
