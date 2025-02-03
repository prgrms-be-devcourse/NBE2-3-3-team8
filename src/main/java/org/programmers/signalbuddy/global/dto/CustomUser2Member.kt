package org.programmers.signalbuddy.global.dto

import org.programmers.signalbuddy.domain.member.entity.enums.MemberRole
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.programmers.signalbuddy.global.security.basic.CustomUserDetails
import org.programmers.signalbuddy.global.security.oauth.CustomOAuth2User

class CustomUser2Member {
    var memberId: Long? = null
    var email: String? = null
    var profileImageUrl: String? = null
    var nickname: String? = null
    var role: MemberRole? = null
    var status: MemberStatus? = null

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

    constructor(arg:String) {}

}
