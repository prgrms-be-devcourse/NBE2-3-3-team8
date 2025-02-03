package org.programmers.signalbuddy.global.security.oauth.response


data class NaverResponse(private val attribute: Map<String, Any>) : OAuth2Response {

    private val attributeResponse = (attribute["response"] as Map<String, Any>)

    override val provider: String = "naver"
    override val providerId: String = attributeResponse["id"].toString()
    override val email: String = attributeResponse["email"].toString()
    override val name: String = attributeResponse["nickname"].toString()
}
