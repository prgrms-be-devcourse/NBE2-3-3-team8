package org.programmers.signalbuddy.domain.email.dto

data class VerifyCodeRequest (
  val email: String,
  val code: String
)