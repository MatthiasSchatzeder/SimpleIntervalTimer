package com.example.simpleintervaltimer.timer.data.data_sources

import androidx.datastore.core.DataStore
import com.example.simpleintervaltimer.timer.data.datastore.TimerSettings
import com.example.simpleintervaltimer.timer.domain.models.TimeInterval
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TimerSettingsLocalDataSource(
    private val timerSettingsDataStore: DataStore<TimerSettings>,
    private val dispatcher: CoroutineDispatcher
) {
    val timerSettingsFlow: Flow<TimerSettings> = timerSettingsDataStore.data

    suspend fun updateQuickStartTimeInterval(timeInterval: TimeInterval) = withContext(dispatcher) {
        timerSettingsDataStore.updateData { timerSettings ->
            timerSettings.copy(quickStartTimeInterval = timeInterval)
        }
    }
}
