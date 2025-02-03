package org.programmers.signalbuddy.domain.feedback.dto

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants.ComponentModel
import org.mapstruct.NullValueMappingStrategy
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers
import org.programmers.signalbuddy.domain.feedback.entity.Feedback
import org.programmers.signalbuddy.domain.member.dto.MemberResponse
import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.global.config.MapStructConfig

@Mapper(config = MapStructConfig::class)
interface FeedbackMapper {

    companion object {
        val INSTANCE: FeedbackMapper = Mappers.getMapper(FeedbackMapper::class.java)
    }

    fun toMemberResponse(member: Member): MemberResponse

    fun toResponse(feedback: Feedback): FeedbackResponse
}
