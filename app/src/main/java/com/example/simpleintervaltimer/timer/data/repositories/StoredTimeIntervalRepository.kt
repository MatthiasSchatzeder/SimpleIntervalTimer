package com.example.simpleintervaltimer.timer.data.repositories

import com.example.simpleintervaltimer.timer.data.data_sources.StoredTimeIntervalLocalDataSource
import com.example.simpleintervaltimer.timer.data.db.realm_objects.StoredTimeInterval
import org.mongodb.kbson.ObjectId

class StoredTimeIntervalRepository(
	private val storedTimeIntervalLocalDataSource: StoredTimeIntervalLocalDataSource = StoredTimeIntervalLocalDataSource(),
) {
	val storedTimeIntervalsFlow = storedTimeIntervalLocalDataSource.storedTimeIntervalsFlow

	suspend fun addStoredTimeInterval(storedTimeInterval: StoredTimeInterval) {
		storedTimeIntervalLocalDataSource.addStoredTimeInterval(storedTimeInterval)
	}

	suspend fun getStoredTimeInterval(storedTimeIntervalId: ObjectId): StoredTimeInterval? {
		return storedTimeIntervalLocalDataSource.getStoredTimeInterval(storedTimeIntervalId)
	}

	suspend fun updateStoredTimeInterval(storedTimeInterval: StoredTimeInterval) {
		storedTimeIntervalLocalDataSource.updateStoredTimeInterval(storedTimeInterval)
	}

	suspend fun deleteStoredTimeInterval(storedTimeInterval: StoredTimeInterval) {
		storedTimeIntervalLocalDataSource.deleteStoredTimeInterval(storedTimeInterval)
	}
}
