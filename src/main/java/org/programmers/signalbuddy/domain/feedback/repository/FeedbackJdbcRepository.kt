package org.programmers.signalbuddy.domain.feedback.repository

import lombok.RequiredArgsConstructor
import org.programmers.signalbuddy.domain.feedback.dto.FeedbackResponse
import org.programmers.signalbuddy.domain.feedback.entity.enums.AnswerStatus
import org.programmers.signalbuddy.domain.member.dto.MemberResponse
import org.programmers.signalbuddy.domain.member.entity.enums.MemberRole
import org.programmers.signalbuddy.domain.member.entity.enums.MemberStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

@Repository
class FeedbackJdbcRepository (
    private val jdbcTemplate: JdbcTemplate
) {

    fun fullTextSearch(
        pageable: Pageable,
        keyword: String?,
        answerStatus: Long
    ): Page<FeedbackResponse> {
        val query = StringBuilder()
        query
            .append(
                "SELECT f.feedback_id, f.subject, f.content, f.like_count, f.answer_status, f.created_at, f.updated_at, "
            )
            .append(
                "m.member_id, m.email, m.nickname, m.profile_image_url, m.role, m.member_status "
            )
            .append("FROM feedbacks f ")
            .append("JOIN members m ON f.member_id = m.member_id ")
            .append("WHERE MATCH(f.subject, f.content) AGAINST (? IN NATURAL LANGUAGE MODE) ")
            .append("AND ")
            .append(answerStatusCondition(answerStatus))
            .append("LIMIT ? OFFSET ?;")

        val countQuery = StringBuilder()
        countQuery
            .append("SELECT count(*) ")
            .append("FROM feedbacks f ")
            .append("JOIN members m ON f.member_id = m.member_id ")
            .append("WHERE MATCH(f.subject, f.content) AGAINST (? IN NATURAL LANGUAGE MODE) ")
            .append("AND ")
            .append(answerStatusCondition(answerStatus))

        val feedbacks = jdbcTemplate.query(
            query.toString(), FeedbackResponseRowMapper(), keyword, pageable.pageSize,
            pageable.offset
        )

        val total = jdbcTemplate.queryForObject(
            countQuery.toString(),
            Long::class.java, keyword
        ) ?: 0L

        return PageImpl(feedbacks, pageable, total)
    }

    private fun answerStatusCondition(answerStatus: Long): String {
        var predicate = " "

        when (answerStatus) {
            0L -> { predicate = "answer_status = 'BEFORE' " }
            1L -> { predicate = "answer_status = 'COMPLETION' " }
        }

        return predicate
    }

    private class FeedbackResponseRowMapper : RowMapper<FeedbackResponse> {
        @Throws(SQLException::class)
        override fun mapRow(rs: ResultSet, rowNum: Int): FeedbackResponse {
            val memberResponse = MemberResponse(
                memberId = rs.getLong("member_id"),
                email = rs.getString("email"),
                nickname = rs.getString("nickname"),
                profileImageUrl = rs.getString("profile_image_url"),
                role = MemberRole.valueOf(rs.getString("role")),
                memberStatus = MemberStatus.valueOf(rs.getString("member_status"))
            )

            val feedbackResponse = FeedbackResponse(
                feedbackId = rs.getLong("feedback_id"),
                subject = rs.getString("subject"),
                content = rs.getString("content"),
                likeCount = rs.getLong("like_count"),
                answerStatus = AnswerStatus.valueOf(rs.getString("answer_status")),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                updatedAt = rs.getTimestamp("updated_at").toLocalDateTime(),
                member = memberResponse
            )

            return feedbackResponse
        }
    }
}
