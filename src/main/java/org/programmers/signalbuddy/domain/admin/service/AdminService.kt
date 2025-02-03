package org.programmers.signalbuddy.domain.member.service

import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.programmers.signalbuddy.domain.admin.dto.AdminMemberResponse
import org.programmers.signalbuddy.domain.admin.mapper.AdminMapper
import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.exception.AdminErrorCode
import org.programmers.signalbuddy.domain.member.repository.MemberRepository
import org.programmers.signalbuddy.global.exception.BusinessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminService(
    private val memberRepository: MemberRepository,
) {
    val converter = Mappers.getMapper(AdminMapper::class.java)

    @Transactional(readOnly = true)
    fun getMemberById(id: Long): Member {
        return memberRepository.findById(id)
            .orElseThrow { BusinessException(AdminErrorCode.NOT_FOUND_MEMBER) }
    }

    // 전체 사용자 조회하기
    fun getAllMembers(pageable: Pageable): List<AdminMemberResponse> {
        //TODO : 북마크 추가
        val members: Page<Member> = memberRepository.findAll(pageable)
        return members.content.map { converter.toAdminDto(it) }
    }

    // 사용자 상세 조회
    fun getMember(id: Long): AdminMemberResponse {
        val member: Member = memberRepository.findById(id)
            .orElseThrow { BusinessException(AdminErrorCode.NOT_FOUND_MEMBER) }
        return converter.toAdminDto(member);
    }
}
