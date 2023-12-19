package org.jcma.sharingscores.routes

import arrow.core.raise.recover
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jcma.sharingscores.models.EmptyInput
import org.jcma.sharingscores.models.ScoreResponse
import org.jcma.sharingscores.models.SharingScoresRequest
import org.jcma.sharingscores.services.ScoreManagement

fun Routing.score(scoreManagement: ScoreManagement) {
  post("/sharing-score") { _ ->
    recover({
      val request = call.receiveNullable<SharingScoresRequest>() ?: raise(EmptyInput)
      val score = scoreManagement.shareScore(request.input)
      call.respond(ScoreResponse(score.blocks.joinToString(separator = "\n") { it.toString() }))
    }) {
      handleCqlError(it)
    }
  }
}

// @Resource("/sharingscore")
// class ScoreRoute {
//    companion object :
//        ResourceDescription by describeResource({
//            summary = "Score sharing application"
//            description = "Score sharing application"
//            tags += "Score sharing"
//            post {
//                description = "Share a score located in Google Drive"
//                body {
//                    description = "The url of the score to share"
//                    required = true
//                    json {
//                        schema(
//                            typeOf<SharingScoresRequest>(),
//                            SharingScoresRequest("The url of the score to share")
//                        )
//                    }
//                }
//                HttpStatusCode.OK.value response
//                        {
//                            description = "The content of the score"
//                            json {
//                                schema(
//                                    typeOf<ScoreResponse>(),
//                                    ScoreResponse(
//                                        "The content of the score"
//                                    )
//                                )
//                            }
//                        }
//                HttpStatusCode.BadRequest.value response {}
//                HttpStatusCode.InternalServerError.value response {}
//                HttpStatusCode.ServiceUnavailable.value response {}
//            }
//        })
// }
