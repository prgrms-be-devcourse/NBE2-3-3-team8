package org.programmers.signalbuddy.domain.feedback.dto

import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.programmers.signalbuddy.domain.feedback.entity.Feedback
import org.programmers.signalbuddy.domain.member.dto.MemberResponse
import org.programmers.signalbuddy.domain.member.entity.Member

@Mapper
interface FeedbackMapper {
    fun toMemberResponse(member: Member): MemberResponse

    fun toResponse(feedback: Feedback): FeedbackResponse

    companion object {
        val INSTANCE: FeedbackMapper = Mappers.getMapper(FeedbackMapper::class.java)
    }
}
