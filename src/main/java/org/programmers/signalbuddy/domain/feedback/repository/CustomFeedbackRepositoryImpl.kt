package org.programmers.signalbuddy.domain.feedback.repository

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.QBean
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackResponse
import org.programmers.signalbuddy.domain.feedback.entity.QFeedback.feedback
import org.programmers.signalbuddy.domain.feedback.entity.enums.AnswerStatus
import org.programmers.signalbuddy.domain.member.dto.MemberResponse
import org.programmers.signalbuddy.domain.member.entity.Member
import org.programmers.signalbuddy.domain.member.entity.QMember.member
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.programmers.signalbuddy.global.util.QueryDslUtil.betweenDates
import org.programmers.signalbuddy.global.util.QueryDslUtil.getOrderSpecifiers
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class CustomFeedbackRepositoryImpl (
    private val jpaQueryFactory: JPAQueryFactory
) : CustomFeedbackRepository {

    override fun findAllByActiveMembers(
        pageable: Pageable,
        answerStatus: Long?
    ): Page<FeedbackResponse?> {
        val answerStatusCondition = answerStatusCondition(answerStatus)

        val results = jpaQueryFactory
            .select(feedbackResponseDto).from(feedback)
            .join(member).on(feedback.member.eq(member)).fetchJoin()
            .where(member.memberStatus.eq(MemberStatus.ACTIVITY).and(answerStatusCondition))
            .offset(pageable.offset).limit(pageable.pageSize.toLong())
            .orderBy(OrderSpecifier(Order.DESC, feedback.createdAt))
            .fetch()

        val count = jpaQueryFactory
            .select(feedback.count()).from(feedback)
            .join(member).on(feedback.member.eq(member)).fetchJoin()
            .where(
                member.memberStatus.eq(MemberStatus.ACTIVITY).and(answerStatusCondition)
            )
            .fetchOne() ?: 0

        return PageImpl<FeedbackResponse>(results, pageable, count)
    }

    override fun findPagedByMember(memberId: Long, pageable: Pageable): Page<FeedbackResponse?> {
        val responses = jpaQueryFactory
            .select(feedbackResponseDto)
            .from(feedback).join<Member>(member)
            .on(
                feedback.member.eq(member)
                    .and(member.memberId.eq(memberId))
            )
            .offset(pageable.offset).limit(pageable.pageSize.toLong())
            .orderBy(OrderSpecifier(Order.DESC, feedback.createdAt))
            .fetch()
        val count =
            jpaQueryFactory.select(feedback.count())
                .from(feedback)
                .join(member)
                .on(
                    feedback.member.eq(member)
                        .and(member.memberId.eq(memberId))
                )
                .fetchOne() ?: 0
        return PageImpl<FeedbackResponse>(responses, pageable, count)
    }

    override fun findAll(
        pageable: Pageable,
        startDate: LocalDate?, endDate: LocalDate?, answerStatus: Long?
    ): Page<FeedbackResponse?> {
        val betweenDates = betweenDates(feedback.createdAt, startDate, endDate)
        val answerStatusCondition = answerStatusCondition(answerStatus)

        val results = jpaQueryFactory
            .select(feedbackResponseDto).from(feedback)
            .join(member).on(feedback.member.eq(member))
            .fetchJoin()
            .where(betweenDates.and(answerStatusCondition(answerStatus)))
            .offset(pageable.offset).limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifiers(pageable, feedback.type, "feedback")).fetch()

        val count = jpaQueryFactory
            .select(feedback.count())
            .from(feedback)
            .join(member).on(feedback.member.eq(member))
            .fetchJoin()
            .where(betweenDates.and(answerStatusCondition))
            .fetchOne() ?: 0

        return PageImpl<FeedbackResponse>(results, pageable, count)
    }

    private fun answerStatusCondition(answerStatus: Long?): BooleanExpression {
        var predicate: BooleanExpression? = null

        when (answerStatus) {
            // 답변 전
            0L -> { predicate = feedback.answerStatus.eq(AnswerStatus.BEFORE) }
            // 답변 완료
            1L -> { predicate = feedback.answerStatus.eq(AnswerStatus.COMPLETION) }

        }
        return predicate ?: Expressions.TRUE
    }

    companion object {
        private val memberResponseDto: QBean<MemberResponse> = Projections.fields(
            MemberResponse::class.java,
            member.memberId,
            member.email,
            member.nickname,
            member.profileImageUrl,
            member.role,
            member.memberStatus
        )

        private val feedbackResponseDto: QBean<FeedbackResponse> =
            Projections.fields(
                FeedbackResponse::class.java,
                feedback.feedbackId,
                feedback.subject,
                feedback.content,
                feedback.likeCount,
                feedback.answerStatus,
                feedback.createdAt,
                feedback.updatedAt,
                memberResponseDto.`as`("member")
            )

        private val feedbackNoMemberDto: QBean<FeedbackResponse> =
            Projections.fields(
                FeedbackResponse::class.java,
                feedback.feedbackId,
                feedback.subject,
                feedback.content,
                feedback.likeCount,
                feedback.createdAt,
                feedback.updatedAt
            )
    }
}
