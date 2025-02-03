package org.programmers.signalbuddy.domain.member.repository

import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.exception.MemberErrorCode
import org.programmers.signalbuddy.global.exception.BusinessException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

fun MemberRepository.findByIdOrThrow(memberId: Long) = findByIdOrNull(memberId)
    ?: throw BusinessException(MemberErrorCode.NOT_FOUND_MEMBER)

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    fun existsByEmail(email: String): Boolean

    fun findByEmail(email: String): Member?

}