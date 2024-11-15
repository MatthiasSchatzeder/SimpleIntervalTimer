package com.example.simpleintervaltimer.timer.data.repositories

import com.example.simpleintervaltimer.timer.data.data_sources.StoredTimeIntervalLocalDataSource
import com.example.simpleintervaltimer.timer.data.db.realm_objects.StoredTimeInterval

class StoredTimeIntervalRepository(
    private val storedTimeIntervalLocalDataSource: StoredTimeIntervalLocalDataSource = StoredTimeIntervalLocalDataSource(),
) {
    val storedTimeIntervalsFlow = storedTimeIntervalLocalDataSource.storedTimeIntervalsFlow

    suspend fun addStoredTimeInterval(storedTimeInterval: StoredTimeInterval) {
        storedTimeIntervalLocalDataSource.addStoredTimeInterval(storedTimeInterval)
    }

    suspend fun deleteStoredTimeInterval(storedTimeInterval: StoredTimeInterval) {
        storedTimeIntervalLocalDataSource.deleteStoredTimeInterval(storedTimeInterval)
    }
}
