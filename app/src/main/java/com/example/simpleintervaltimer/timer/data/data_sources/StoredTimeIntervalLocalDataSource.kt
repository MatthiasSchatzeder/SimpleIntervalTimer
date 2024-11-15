package com.example.simpleintervaltimer.timer.data.data_sources

import com.example.simpleintervaltimer.timer.data.db.RealmProvider
import com.example.simpleintervaltimer.timer.data.db.realm_objects.StoredTimeInterval
import io.realm.kotlin.Realm
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

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
