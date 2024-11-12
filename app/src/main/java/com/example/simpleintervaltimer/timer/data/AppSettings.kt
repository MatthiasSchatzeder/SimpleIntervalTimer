package com.example.simpleintervaltimer.timer.data

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

val Context.appSettingsDataStore by dataStore("app-settings.json", AppSettingsSerializer)

@Serializable
data class AppSettings(
    val quickStartTimeInterval: TimeInterval = TimeInterval(30_000, 30_000, 10)
)

private object AppSettingsSerializer : Serializer<AppSettings> {
    override val defaultValue = AppSettings()

    override suspend fun readFrom(input: InputStream): AppSettings {
        try {
            return Json.decodeFromString(
                AppSettings.serializer(), input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Unable to read AppSettings", exception)
        }
    }

    override suspend fun writeTo(appSettings: AppSettings, output: OutputStream) {
        output.write(
            Json.encodeToString(AppSettings.serializer(), appSettings)
                .encodeToByteArray()
        )
    }
}
