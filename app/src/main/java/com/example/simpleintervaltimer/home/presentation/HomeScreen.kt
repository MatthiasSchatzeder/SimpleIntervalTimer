package com.example.simpleintervaltimer.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.simpleintervaltimer.timer.data.TimeInterval

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onStartTimer: (timeInterval: TimeInterval) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Home")
        Button(onClick = { onStartTimer(TimeInterval(5_000, 2_000, 2)) }) {
            Text("Start Timer")
        }
    }
}
