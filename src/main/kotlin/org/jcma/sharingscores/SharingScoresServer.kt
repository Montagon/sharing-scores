package org.jcma.sharingscores

import arrow.continuations.SuspendApp
import arrow.continuations.ktor.server
import arrow.fx.coroutines.resourceScope
import guru.zoroark.tegral.openapi.ktor.TegralOpenApiKtor
import guru.zoroark.tegral.openapi.ktor.openApiEndpoint
import guru.zoroark.tegral.openapi.ktorui.TegralSwaggerUiKtor
import guru.zoroark.tegral.openapi.ktorui.swaggerUiEndpoint
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.resources.Resources
import io.ktor.server.routing.*
import kotlinx.coroutines.awaitCancellation
import org.jcma.sharingscores.config.Environment
import org.jcma.sharingscores.models.Instrument
import org.jcma.sharingscores.routes.score
import org.jcma.sharingscores.services.GCloudSharing
import org.jcma.sharingscores.services.ScoreManagement

object SharingScoresServer {

  @JvmStatic
  fun main(args: Array<String>) = SuspendApp {
    resourceScope {
      val env = Environment.loadEnvironment()

      val gCloudSharing =
        GCloudSharing.create(
          env.googleCloud.tokensDirectoryPath,
          env.googleCloud.clientId,
          env.googleCloud.authUri,
          env.googleCloud.tokenUri,
          env.googleCloud.clientSecret
        )

      // Read the instruments from resources
      val instruments = Instrument.load()

      val scoreManagement = ScoreManagement(gCloudSharing, instruments)

      server(factory = Netty, port = 8080, host = "0.0.0.0") {
        install(CORS) {
          allowNonSimpleContentTypes = true
          HttpMethod.DefaultMethods.forEach { allowMethod(it) }
          allowHeaders { true }
          anyHost()
        }
        install(ContentNegotiation) { json() }
        install(Resources)
        install(TegralOpenApiKtor) {
          title = "Sharing Scores"
          description = "Sharing Scores application"
          version = "0.0.1"
        }
        install(TegralSwaggerUiKtor)
        routing {
          openApiEndpoint("/openapi")
          score(scoreManagement)
          swaggerUiEndpoint(path = "/docs", openApiPath = "/openapi")
        }
      }
      awaitCancellation()
    }
  }
}
