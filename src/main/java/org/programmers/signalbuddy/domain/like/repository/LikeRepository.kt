package org.programmers.signalbuddy.domain.like.repository

import org.programmers.signalbuddy.domain.like.entity.Like
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface LikeRepository : JpaRepository<Like?, Long?> {
    @Query(
        "SELECT CASE  WHEN count(*) > 0 THEN true ELSE false END "
                + "FROM likes l "
                + "WHERE l.member.memberId = :memberId AND l.feedback.feedbackId = :feedbackId"
    )
    fun existsByMemberAndFeedback(
        @Param("memberId") memberId: Long, @Param("feedbackId") feedbackId: Long
    ): Boolean
}
