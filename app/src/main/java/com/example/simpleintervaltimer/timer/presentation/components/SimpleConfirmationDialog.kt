package com.example.simpleintervaltimer.timer.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.simpleintervaltimer.ui.theme.SimpleintervaltimerTheme

@Composable
fun SimpleConfirmationDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean,
    title: String,
    text: String? = null,
    confirmButtonText: String,
    dismissButtonText: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    if (!showDialog) return
    AlertDialog(
        modifier = modifier,
        title = { Text(title) },
        text = { text?.let { Text(it) } },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                }
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismissRequest() }
            ) {
                Text(dismissButtonText)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SimpleConfirmationDialogPreview() {
    SimpleintervaltimerTheme {
        SimpleConfirmationDialog(
            showDialog = true,
            title = "Title: this is some long title of this button that is very long",
            confirmButtonText = "Confirm",
            dismissButtonText = "Dismiss",
            onConfirm = {},
            onDismissRequest = {}
        )
    }
}
