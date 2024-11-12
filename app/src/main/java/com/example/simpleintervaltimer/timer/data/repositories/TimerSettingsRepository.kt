package com.example.simpleintervaltimer.timer.data.repositories

import com.example.simpleintervaltimer.timer.data.data_sources.TimerSettingsLocalDataSource
import com.example.simpleintervaltimer.timer.data.datastore.TimerSettings
import com.example.simpleintervaltimer.timer.domain.models.TimeInterval
import kotlinx.coroutines.flow.Flow

class TimerSettingsRepository(
    private val timerSettingsLocalDataSource: TimerSettingsLocalDataSource
) {
    val timerSettingsFlow: Flow<TimerSettings> = timerSettingsLocalDataSource.timerSettingsFlow

    suspend fun updateQuickStartTimeInterval(timeInterval: TimeInterval) {
        timerSettingsLocalDataSource.updateQuickStartTimeInterval(timeInterval)
    }
}
