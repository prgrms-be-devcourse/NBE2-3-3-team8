package org.programmers.signalbuddy.global.security.oauth.response


data class GoogleResponse( private val attribute: Map<String, Any> ) : OAuth2Response {

    override val provider: String = "google"
    override val providerId: String = attribute["sub"].toString()
    override val email: String = attribute["email"].toString()
    override val name: String = attribute["name"].toString()
}
