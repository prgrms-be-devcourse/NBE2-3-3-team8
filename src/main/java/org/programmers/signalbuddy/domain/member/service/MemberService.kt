package org.programmers.signalbuddy.domain.member.service

import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.exception.MemberErrorCode
import org.programmers.signalbuddy.domain.member.repository.MemberRepository
import org.programmers.signalbuddy.global.exception.BusinessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
) {
    @Transactional(readOnly = true)
    fun getMemberById(id: Long): Member {
        return memberRepository.findById(id)
            .orElseThrow { BusinessException(MemberErrorCode.NOT_FOUND_MEMBER) }
    }
}
