package org.jcma.sharingscores.models

sealed interface ScoreError {
  fun message(): String
}

object GenericError : ScoreError {
  override fun message(): String = "An error occurred"
}

object EmptyInput : ScoreError {
  override fun message(): String = "The request was empty"
}
