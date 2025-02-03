package org.programmers.signalbuddy.domain.admin.mapper

import org.mapstruct.Mapper
import org.programmers.signalbuddy.domain.admin.dto.AdminMemberResponse
import org.programmers.signalbuddy.domain.member.entity.Member
@Mapper(componentModel = "spring")
interface AdminMapper {

    fun toAdminDto(member: Member): AdminMemberResponse
    fun toAdminDtoList(members: List<Member>): List<AdminMemberResponse>

}
