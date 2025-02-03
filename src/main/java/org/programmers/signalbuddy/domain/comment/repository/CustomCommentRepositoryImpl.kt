package org.programmers.signalbuddy.domain.comment.repository

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.programmers.signalbuddy.domain.comment.dto.CommentResponse
import org.programmers.signalbuddy.domain.comment.entity.QComment.comment
import org.programmers.signalbuddy.domain.member.dto.MemberResponse
import org.programmers.signalbuddy.domain.member.entity.QMember.member
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class CustomCommentRepositoryImpl (
    private val jpaQueryFactory: JPAQueryFactory
) : CustomCommentRepository {

    override fun findAllByFeedbackIdAndActiveMembers(
        feedbackId: Long,
        pageable: Pageable
    ): Page<CommentResponse?> {
        val results = jpaQueryFactory
            .select(commentResponseDto).from(comment)
            .join(member).on(comment.member.eq(member)).fetchJoin()
            .where(
                member.memberStatus.eq(MemberStatus.ACTIVITY)
                    .and(comment.feedback.feedbackId.eq(feedbackId))
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(OrderSpecifier(Order.ASC, comment.createdAt)).fetch()

        val count = jpaQueryFactory
            .select(comment.count())
            .from(comment)
            .join(member).on(comment.member.eq(member)).fetchJoin()
            .where(
                member.memberStatus.eq(MemberStatus.ACTIVITY)
                    .and(comment.feedback.feedbackId.eq(feedbackId))
            )
            .fetchOne() ?: 0L

        return PageImpl<CommentResponse>(results, pageable, count)
    }

    companion object {
        private val memberResponseDto = Projections.constructor(
            MemberResponse::class.java,
            member.memberId,
            member.email,
            member.nickname,
            member.profileImageUrl,
            member.role,
            member.memberStatus
        )

        private val commentResponseDto = Projections.constructor(
            CommentResponse::class.java,
            comment.commentId, comment.content,
            comment.createdAt, comment.updatedAt,
            memberResponseDto
        )
    }
}
