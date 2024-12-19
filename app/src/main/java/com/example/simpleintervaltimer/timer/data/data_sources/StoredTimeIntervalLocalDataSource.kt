package com.example.simpleintervaltimer.timer.data.data_sources

import com.example.simpleintervaltimer.timer.data.db.RealmProvider
import com.example.simpleintervaltimer.timer.data.db.realm_objects.StoredTimeInterval
import io.realm.kotlin.Realm
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId

class StoredTimeIntervalLocalDataSource(
    private val realm: Realm = RealmProvider.getRealm(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    val storedTimeIntervalsFlow = realm.query(StoredTimeInterval::class)
        .asFlow()
        .map { result ->
            result.list.toList()
        }

    suspend fun addStoredTimeInterval(storedTimeInterval: StoredTimeInterval) = withContext(dispatcher) {
        realm.write {
            copyToRealm(storedTimeInterval)
        }
    }

    suspend fun getStoredTimeInterval(storedTimeIntervalId: ObjectId) = withContext(dispatcher) {
        realm.query(
            StoredTimeInterval::class,
            "_id == $0",
            storedTimeIntervalId
        ).first().find()
    }

    suspend fun updateStoredTimeInterval(storedTimeInterval: StoredTimeInterval) = withContext(dispatcher) {
        realm.write {
            query(
                StoredTimeInterval::class,
                "_id == $0",
                storedTimeInterval._id
            ).find().firstOrNull()?.also { toUpdate ->
                toUpdate.name = storedTimeInterval.name
                toUpdate.workTime = storedTimeInterval.workTime
                toUpdate.restTime = storedTimeInterval.restTime
                toUpdate.intervals = storedTimeInterval.intervals
            }
        }
    }

    suspend fun deleteStoredTimeInterval(storedTimeInterval: StoredTimeInterval) = withContext(dispatcher) {
        realm.write {
            val toDelete = query(
                StoredTimeInterval::class,
                "_id == $0",
                storedTimeInterval._id
            ).find().firstOrNull() ?: return@write
            delete(toDelete)
        }
    }
}
