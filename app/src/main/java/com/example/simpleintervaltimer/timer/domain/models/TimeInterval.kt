package com.example.simpleintervaltimer.timer.domain.models

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.example.simpleintervaltimer.common.helper.getDisplayMinutes
import com.example.simpleintervaltimer.common.helper.getDisplaySeconds
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class TimeInterval(val workTime: Long, val restTime: Long, val intervals: Int) {

    fun getDisplayWorkTime() = "%02d:%02d".format(workTime.getDisplayMinutes(), workTime.getDisplaySeconds())

    fun getDisplayRestTime() = "%02d:%02d".format(restTime.getDisplayMinutes(), restTime.getDisplaySeconds())

    /**
     * Custom NavType for TimeInterval used for serializing and deserializing TimeInterval objects
     */
    object CustomNavType : NavType<TimeInterval>(isNullableAllowed = false) {
        override fun parseValue(value: String): TimeInterval {
            return Json.decodeFromString<TimeInterval>(Uri.decode(value))
        }

        override fun serializeAsValue(value: TimeInterval): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun get(bundle: Bundle, key: String): TimeInterval? {
            return Json.decodeFromString<TimeInterval?>(bundle.getString(key) ?: return null)
        }

        override fun put(bundle: Bundle, key: String, value: TimeInterval) {
            bundle.putString(key, Json.encodeToString(value))
        }
    }
}
