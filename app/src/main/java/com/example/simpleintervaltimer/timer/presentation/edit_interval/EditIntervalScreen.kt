package com.example.simpleintervaltimer.timer.presentation.edit_interval

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simpleintervaltimer.R
import com.example.simpleintervaltimer.timer.domain.models.TimeInterval
import com.example.simpleintervaltimer.timer.presentation.components.LoadingScreen
import com.example.simpleintervaltimer.timer.presentation.components.SimpleConfirmationDialog
import com.example.simpleintervaltimer.timer.presentation.components.time_interval_input.TimeIntervalInput
import com.example.simpleintervaltimer.ui.theme.SimpleIntervalTimerTheme

@Composable
fun EditStoredTimeIntervalScreen(
	modifier: Modifier = Modifier,
	storedTimeIntervalIdHexString: String,
	editIntervalViewModel: EditIntervalViewModel = viewModel(
		factory = EditIntervalViewModelFactory(storedTimeIntervalIdHexString)
	),
	onEditFinished: () -> Unit
) {
	BackHandler {
		editIntervalViewModel.cancelEdit(onEditFinished)
	}
	val uiState by editIntervalViewModel.uiState.collectAsStateWithLifecycle()
	val name = uiState.name
	val initialTimeInterval = uiState.timeInterval
	if (uiState.isLoading || name == null || initialTimeInterval == null) {
		LoadingScreen(modifier)
		return
	}
	SimpleConfirmationDialog(
		showDialog = uiState.showDiscardChangesDialog,
		title = stringResource(R.string.discard_changes_title),
		text = stringResource(R.string.discard_changes_message),
		confirmButtonText = stringResource(R.string.discard),
		dismissButtonText = stringResource(R.string.cancel),
		onConfirm = onEditFinished,
		onDismissRequest = { editIntervalViewModel.dismissDiscardChangesDialog() }
	)
	EditTimeInterval(
		modifier = modifier,
		name = name,
		initialTimeInterval = initialTimeInterval,
		onNameChanged = { editIntervalViewModel.onNameChanged(it) },
		onTimeIntervalChanged = { editIntervalViewModel.onTimeIntervalChanged(it) },
		onCancelRequested = { editIntervalViewModel.cancelEdit(onEditFinished) },
		onSaveRequested = { editIntervalViewModel.saveChanges(onEditFinished) }
	)
}

@Composable
private fun EditTimeInterval(
	modifier: Modifier = Modifier,
	name: String,
	initialTimeInterval: TimeInterval,
	onNameChanged: (String) -> Unit,
	onTimeIntervalChanged: (TimeInterval) -> Unit,
	onCancelRequested: () -> Unit,
	onSaveRequested: () -> Unit
) {
	Box(
		modifier = modifier
			.fillMaxSize()
			.padding(8.dp)
	) {
		IconButton(
			modifier = Modifier.align(Alignment.TopStart),
			onClick = onCancelRequested,
			content = {
				Icon(
					modifier = Modifier.size(48.dp),
					imageVector = Icons.Default.Close,
					contentDescription = stringResource(R.string.cancel)
				)
			}
		)
		Column(
			modifier = Modifier
				.align(Alignment.Center)
				.width(200.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			OutlinedTextField(
				modifier = Modifier.fillMaxWidth(),
				value = name,
				textStyle = TextStyle(fontSize = 30.sp),
				onValueChange = onNameChanged,
				label = { Text(stringResource(R.string.name)) }
			)
			Spacer(Modifier.height(8.dp))
			TimeIntervalInput(
				initialTimeInterval = initialTimeInterval,
				onTimeIntervalChanged = onTimeIntervalChanged
			)
			Spacer(Modifier.height(8.dp))
			Button(
				modifier = Modifier.fillMaxWidth(),
				onClick = onSaveRequested
			) {
				Text(
					text = stringResource(R.string.save),
					fontSize = 30.sp
				)
			}
		}
	}
}

@PreviewFontScale
@PreviewScreenSizes
@Preview
@Composable
fun EditStoredTimeIntervalScreenPreview() {
	SimpleIntervalTimerTheme {
		EditTimeInterval(
			name = "test",
			initialTimeInterval = TimeInterval(30_000, 30_000, 10),
			onNameChanged = {},
			onTimeIntervalChanged = {},
			onCancelRequested = {},
			onSaveRequested = {}
		)
	}
}
