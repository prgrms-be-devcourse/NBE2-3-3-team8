package org.programmers.signalbuddy.domain.like.service

import org.programmers.signalbuddy.domain.like.dto.LikeExistResponse
import org.programmers.signalbuddy.domain.like.dto.LikeRequestType
import org.programmers.signalbuddy.domain.like.dto.LikeUpdateRequest
import org.programmers.signalbuddy.domain.like.exception.LikeErrorCode
import org.programmers.signalbuddy.domain.like.repository.LikeRepository
import org.programmers.signalbuddy.global.dto.CustomUser2Member
import org.programmers.signalbuddy.global.exception.BusinessException
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
@Transactional(readOnly = true)
class LikeService (
    private val likeRepository: LikeRepository,
    private val redisTemplate: StringRedisTemplate
) {

    companion object {
        const val likeKeyPrefix: String = "like:"

        fun generateKey(request: LikeUpdateRequest): String {
            return likeKeyPrefix + request.feedbackId + ":" + request.memberId
        }
    }

    @Transactional
    fun addLike(feedbackId: Long, user: CustomUser2Member?) {
        val key = generateKey(feedbackId, user?.memberId!!)

        // 삭제 요청 데이터가 Redis에 있을 때
        if (java.lang.Boolean.TRUE == redisTemplate.hasKey(key)) {
            redisTemplate.delete(key)
            return
        }

        val isExisted = likeRepository.existsByMemberAndFeedback(user.memberId!!, feedbackId)
        if (isExisted) {
            throw BusinessException(LikeErrorCode.ALREADY_ADDED_LIKE)
        }

        val operations = redisTemplate.opsForValue()
        operations[key, LikeRequestType.ADD.name, 3L] = TimeUnit.MINUTES
    }

    fun existsLike(feedbackId: Long, user: CustomUser2Member?): LikeExistResponse {
        val isExisted = likeRepository.existsByMemberAndFeedback(user?.memberId!!, feedbackId)
        return LikeExistResponse(isExisted)
    }

    @Transactional
    fun deleteLike(feedbackId: Long, user: CustomUser2Member?) {
        val key = generateKey(feedbackId, user?.memberId!!)

        // 좋아요 데이터가 아직 DB에 저장되지 않은 경우 (Redis에만 있을 때)
        if (java.lang.Boolean.TRUE == redisTemplate.hasKey(key)) {
            redisTemplate.delete(key)
            return
        }

        val isExisted = likeRepository.existsByMemberAndFeedback(user.memberId!!, feedbackId)
        if (!isExisted) {
            throw BusinessException(LikeErrorCode.NOT_FOUND_LIKE)
        }

        val operations = redisTemplate.opsForValue()
        operations[key, LikeRequestType.CANCEL.name, 3L] = TimeUnit.MINUTES
    }

    private fun generateKey(feedbackId: Long, memberId: Long): String {
        return "$likeKeyPrefix$feedbackId:$memberId"
    }
}
