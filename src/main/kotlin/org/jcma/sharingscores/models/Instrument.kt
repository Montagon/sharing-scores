package org.jcma.sharingscores.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Instrument(
    val name: String,
    @SerialName("restrict-level")
    val restrictLevel: String,
    val publishName: String
) {
    companion object {
        fun load(): List<Instrument> =
            this::class.java.getResource("/instruments-config.json")!!.readText().let {
                Json.decodeFromString<List<Instrument>>(it)
            }
    }
}


