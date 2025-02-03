package org.programmers.signalbuddy.global.dto

import lombok.Getter
import org.programmers.signalbuddy.domain.member.entity.enums.MemberRole
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.programmers.signalbuddy.global.security.basic.CustomUserDetails
import org.programmers.signalbuddy.global.security.oauth.CustomOAuth2User

@Getter
class CustomUser2Memberdd {
    private var memberId: Long? = null
    private var email: String? = null
    private var profileImageUrl: String? = null
    private var nickname: String? = null
    private var role: MemberRole? = null
    private var status: MemberStatus? = null

    constructor(customUserDetails: CustomUserDetails) {
        this.memberId = customUserDetails.memberId
        this.email = customUserDetails.email
        this.profileImageUrl = customUserDetails.profileImageUrl
        this.nickname = customUserDetails.nickname
        this.role = customUserDetails.role
        this.status = customUserDetails.status
    }

    constructor(customOAuth2User: CustomOAuth2User) {
        this.memberId = customOAuth2User.memberId
        this.email = customOAuth2User.email
        this.profileImageUrl = customOAuth2User.profileImageUrl
        this.nickname = customOAuth2User.nickname
        this.role = customOAuth2User.role
        this.status = customOAuth2User.status
    }

    constructor(arg: String?)
}
