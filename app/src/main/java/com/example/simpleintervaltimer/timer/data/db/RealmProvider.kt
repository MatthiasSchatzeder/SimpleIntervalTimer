package com.example.simpleintervaltimer.timer.data.db

import com.example.simpleintervaltimer.timer.data.db.realm_objects.StoredTimeInterval
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

object RealmProvider {
    private val config = RealmConfiguration.Builder(
        schema = setOf(
            StoredTimeInterval::class
        )
    ).build()

    private var _realm: Realm? = null

    fun getRealm(): Realm = synchronized(this) {
        _realm?.let { return it }
        _realm = Realm.open(config) // closing the realm instance should not be necessary for now
        return _realm!!
    }
}
