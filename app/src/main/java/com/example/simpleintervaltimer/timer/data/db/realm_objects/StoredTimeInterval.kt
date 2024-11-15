package com.example.simpleintervaltimer.timer.data.db.realm_objects

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class StoredTimeInterval : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name: String = "Time Interval"
    var workTime: Long = 30_000L
    var restTime: Long = 30_000L
    var intervals: Int = 10
}
