package org.programmers.signalbuddy.domain.like.repository

import org.programmers.signalbuddy.domain.like.dto.LikeUpdateRequest
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.SQLException
import java.time.LocalDateTime

@Repository
class LikeJdbcRepository (
    private val jdbcTemplate: JdbcTemplate
) {

    fun saveAllInBatch(likeUpdateRequests: List<LikeUpdateRequest>) {
        val sql = ("INSERT INTO likes (member_id, feedback_id, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?)")

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            @Throws(SQLException::class)
            override fun setValues(ps: PreparedStatement, i: Int) {
                val now = LocalDateTime.now()
                val request = likeUpdateRequests[i]
                ps.setLong(1, request.memberId)
                ps.setLong(2, request.feedbackId)
                ps.setObject(3, now)
                ps.setObject(4, now)
            }

            override fun getBatchSize(): Int = likeUpdateRequests.size
        })
    }

    fun deleteAllByLikeRequestsInBatch(likeUpdateRequests: List<LikeUpdateRequest>) {
        val sql = "DELETE FROM likes WHERE member_id = ? AND feedback_id = ?"

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            @Throws(SQLException::class)
            override fun setValues(ps: PreparedStatement, i: Int) {
                val request = likeUpdateRequests[i]
                ps.setLong(1, request.memberId)
                ps.setLong(2, request.feedbackId)
            }

            override fun getBatchSize(): Int = likeUpdateRequests.size
        })
    }
}
