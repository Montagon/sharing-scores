package org.jcma.sharingscores.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.jcma.sharingscores.models.CqlErrorResponse
import org.jcma.sharingscores.models.EmptyInput
import org.jcma.sharingscores.models.GenericError
import org.jcma.sharingscores.models.ScoreError

internal suspend fun PipelineContext<Unit, ApplicationCall>.handleCqlError(it: ScoreError) {
  when (it) {
    is GenericError ->
      call.respond(status = HttpStatusCode.BadRequest, message = CqlErrorResponse(it.message()))
    EmptyInput ->
      call.respond(status = HttpStatusCode.BadRequest, message = CqlErrorResponse("Empty input"))
  }
}
