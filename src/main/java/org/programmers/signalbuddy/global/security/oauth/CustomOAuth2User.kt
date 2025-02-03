package org.programmers.signalbuddy.global.security.oauth

import org.programmers.signalbuddy.domain.member.entity.enums.MemberRole
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.programmers.signalbuddy.global.security.oauth.response.OAuth2Response
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import java.io.Serializable

class CustomOAuth2User(
    private val oAuth2Response: OAuth2Response,
    val memberId: Long?,
    val email: String,
    val profileImageUrl: String?,
    val nickname: String,
    val role: MemberRole,
    val status: MemberStatus
) : OAuth2User, Serializable {

    override fun getName() = oAuth2Response.name

    override fun getAttributes() = null

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val authorities: MutableCollection<GrantedAuthority> = ArrayList()
        authorities.add(GrantedAuthority { "ROLE_" + role.name })

        return authorities
    }
}
