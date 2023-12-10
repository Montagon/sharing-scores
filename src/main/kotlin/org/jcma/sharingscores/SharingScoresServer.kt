package org.jcma.sharingscores

import arrow.continuations.SuspendApp
import arrow.continuations.ktor.server
import arrow.fx.coroutines.resourceScope
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.awaitCancellation

object SharingScoresServer {

    @JvmStatic
    fun main(args: Array<String>) = SuspendApp {
        resourceScope {
            server(factory = Netty, port = 8080, host = "0.0.0.0") {
                install(CORS) {
                    allowNonSimpleContentTypes = true
                    HttpMethod.DefaultMethods.forEach { allowMethod(it) }
                    allowHeaders { true }
                    anyHost()
                }
                install(ContentNegotiation) { json() }
                install(Resources)
                routing {
                    get("/") {
                        call.respondText("Hello, world!")
                    }
                }
            }
            awaitCancellation()
        }
    }
}