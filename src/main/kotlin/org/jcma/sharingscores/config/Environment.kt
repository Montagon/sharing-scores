package org.jcma.sharingscores.config

import java.lang.System.getenv

data class Environment(
  val googleCloud: GCloudConfig,
) {
  data class GCloudConfig(
    val tokensDirectoryPath: String = getenv("TOKENS_DIRECTORY_PATH") ?: "build/tokens",
    val clientId: String = getenv("CLIENT_ID") ?: "",
    val authUri: String = getenv("AUTH_URI") ?: "",
    val tokenUri: String = getenv("TOKEN_URI") ?: "",
    val clientSecret: String = getenv("CLIENT_SECRET") ?: ""
  )

  companion object {
    fun loadEnvironment(): Environment = Environment(googleCloud = GCloudConfig())
  }
}
