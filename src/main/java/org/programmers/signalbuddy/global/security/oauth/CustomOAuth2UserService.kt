package org.programmers.signalbuddy.global.security.oauth

import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.entity.enums.MemberRole
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.programmers.signalbuddy.domain.member.exception.MemberErrorCode
import org.programmers.signalbuddy.domain.member.repository.MemberRepository
import org.programmers.signalbuddy.domain.social.entity.SocialProvider
import org.programmers.signalbuddy.domain.social.repository.SocialProviderRepository
import org.programmers.signalbuddy.global.exception.BusinessException
import org.programmers.signalbuddy.global.exception.GlobalErrorCode
import org.programmers.signalbuddy.global.security.oauth.response.GoogleResponse
import org.programmers.signalbuddy.global.security.oauth.response.KakaoResponse
import org.programmers.signalbuddy.global.security.oauth.response.NaverResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    val memberRepository: MemberRepository,
    val socialProviderRepository: SocialProviderRepository
) : DefaultOAuth2UserService() {

    @Value("\${default.profile.image.path}")
    private lateinit var defaultProfileImagePath: String

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User: OAuth2User = super.loadUser(userRequest)
        val registrationId = userRequest.clientRegistration.registrationId

        val oAuth2Response = when (registrationId) {
            "naver" -> NaverResponse(oAuth2User.attributes)
            "google" -> GoogleResponse(oAuth2User.attributes)
            "kakao" -> KakaoResponse(oAuth2User.attributes)
            else -> throw BusinessException(GlobalErrorCode.SERVER_ERROR)
        }

        // 기존 사용자 조회, 없으면 소셜의 이메일과 닉네임을 기반으로 새로운 사용자 생성
        val email: String = oAuth2Response.email
        var saveMember = memberRepository.findByEmail(email)
            ?: memberRepository.save(
                Member(
                    email = email,
                    nickname = oAuth2Response.name,
                    profileImageUrl = defaultProfileImagePath,
                    role = MemberRole.USER,
                    memberStatus = MemberStatus.ACTIVITY
                )
            )

        if (saveMember.memberStatus == MemberStatus.WITHDRAWAL) {
            throw BusinessException(MemberErrorCode.WITHDRAWN_MEMBER)
        }

        if (!socialProviderRepository.existsByOauthProviderAndSocialId(
                oAuth2Response.provider,
                oAuth2Response.providerId
            )
        ) {
            socialProviderRepository.save(
                SocialProvider(
                    oauthProvider = oAuth2Response.provider,
                    socialId = oAuth2Response.providerId,
                    member = saveMember
                )
            )
        }

        return CustomOAuth2User(
            oAuth2Response, saveMember.memberId, email,
            saveMember.profileImageUrl, saveMember.nickname,
            saveMember.role, saveMember.memberStatus
        )
    }
}
