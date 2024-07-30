package com.vanillavideoplayer.videoplayer.core.datastore.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.vanillavideoplayer.videoplayer.core.model.PlayerPref
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object PlayerPrefSerializerObj : Serializer<PlayerPref> {

    private val jsonFormatVal = Json { ignoreUnknownKeys = true }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: PlayerPref, output: OutputStream) {
        output.write(
            jsonFormatVal.encodeToString(
                serializer = PlayerPref.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }


    override val defaultValue: PlayerPref
        get() = PlayerPref()

    override suspend fun readFrom(input: InputStream): PlayerPref {
        try {
            return jsonFormatVal.decodeFromString(
                deserializer = PlayerPref.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Cannot read datastore", exception)
        }
    }

}
