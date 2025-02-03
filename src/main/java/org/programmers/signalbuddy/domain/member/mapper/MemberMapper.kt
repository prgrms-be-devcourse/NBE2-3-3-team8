package org.programmers.signalbuddy.domain.member.mapper

import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.programmers.signalbuddy.domain.member.dto.MemberResponse
import org.programmers.signalbuddy.domain.member.entity.Member

@Mapper
interface MemberMapper {
    companion object {
        val INSTANCE: MemberMapper = Mappers.getMapper(MemberMapper::class.java)
    }

    fun toDto(member: Member?): MemberResponse?
}