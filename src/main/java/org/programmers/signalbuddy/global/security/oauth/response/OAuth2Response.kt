package org.programmers.signalbuddy.global.security.oauth.response

import java.io.Serializable

interface OAuth2Response : Serializable {

    val provider: String
    val providerId: String
    val email: String
    val name: String
}
