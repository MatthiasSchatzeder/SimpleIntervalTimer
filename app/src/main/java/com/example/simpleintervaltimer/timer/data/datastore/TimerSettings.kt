package com.example.simpleintervaltimer.timer.data.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.example.simpleintervaltimer.timer.domain.models.TimeInterval
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

val Context.timerSettingsDataStore by dataStore("timer-settings.json", TimerSettingsSerializer)

@Serializable
data class TimerSettings(
    val quickStartTimeInterval: TimeInterval = TimeInterval(30_000, 30_000, 10)
)

private object TimerSettingsSerializer : Serializer<TimerSettings> {
    override val defaultValue = TimerSettings()

    override suspend fun readFrom(input: InputStream): TimerSettings {
        try {
            return Json.decodeFromString(
                TimerSettings.serializer(), input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Unable to read TimerSettings", exception)
        }
    }

    override suspend fun writeTo(timerSettings: TimerSettings, output: OutputStream) {
        output.write(
            Json.encodeToString(TimerSettings.serializer(), timerSettings)
                .encodeToByteArray()
        )
    }
}
