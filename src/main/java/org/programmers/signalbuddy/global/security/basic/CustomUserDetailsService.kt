package org.programmers.signalbuddy.global.security.basic

import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.programmers.signalbuddy.domain.member.exception.MemberErrorCode
import org.programmers.signalbuddy.domain.member.repository.MemberRepository
import org.programmers.signalbuddy.global.exception.BusinessException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val memberRepository: MemberRepository) :
    UserDetailsService {
    override fun loadUserByUsername(email: String): CustomUserDetails {

        val findMember = memberRepository.findByEmail(email) ?: throw BusinessException(
            MemberErrorCode.NOT_FOUND_MEMBER
        )

        if (findMember.memberStatus == MemberStatus.WITHDRAWAL) {
            throw BusinessException(MemberErrorCode.WITHDRAWN_MEMBER)
        }

        return CustomUserDetails(
            findMember.memberId, findMember.email,
            findMember.password, findMember.profileImageUrl, findMember.nickname,
            findMember.role, findMember.memberStatus
        )
    }
}
