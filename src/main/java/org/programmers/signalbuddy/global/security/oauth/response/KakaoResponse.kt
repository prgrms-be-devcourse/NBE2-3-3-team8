package org.programmers.signalbuddy.global.security.oauth.response

data class KakaoResponse( private val attribute: Map<String, Any> ) : OAuth2Response {

    private val kakaoAccountAttributes =
        (attribute["kakao_account"] as Map<String, Any>)
    private val profileAttributes =
        (kakaoAccountAttributes["profile"] as Map<String, Any>)

    override val provider: String = "kakao"
    override val providerId: String = attribute["id"].toString()
    override val email: String = kakaoAccountAttributes["email"].toString()
    override val name: String = profileAttributes["name"].toString()

}
