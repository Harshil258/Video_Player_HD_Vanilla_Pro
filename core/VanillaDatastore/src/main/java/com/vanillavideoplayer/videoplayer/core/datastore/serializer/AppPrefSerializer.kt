package com.vanillavideoplayer.videoplayer.core.datastore.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.vanillavideoplayer.videoplayer.core.model.ApplicationPrefData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AppPrefSerializer : Serializer<ApplicationPrefData> {

    private val jsonFormatVal = Json { ignoreUnknownKeys = true }

    override suspend fun readFrom(input: InputStream): ApplicationPrefData {
        try {
            return jsonFormatVal.decodeFromString(
                deserializer = ApplicationPrefData.serializer(), string = input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Cannot read datastore", exception)
        }
    }

    override val defaultValue: ApplicationPrefData
        get() = ApplicationPrefData()

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: ApplicationPrefData, output: OutputStream) {
        output.write(
            jsonFormatVal.encodeToString(
                serializer = ApplicationPrefData.serializer(), value = t
            ).encodeToByteArray()
        )
    }
}
