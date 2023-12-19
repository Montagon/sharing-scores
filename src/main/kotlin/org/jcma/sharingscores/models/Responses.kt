package org.jcma.sharingscores.models

import kotlinx.serialization.Serializable

@Serializable data class ScoreResponse(val score: String)

@Serializable data class CqlErrorResponse(val error: String, val logs: List<String> = emptyList())
