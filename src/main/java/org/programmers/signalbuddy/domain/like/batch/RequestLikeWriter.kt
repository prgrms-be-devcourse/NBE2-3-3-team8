package org.programmers.signalbuddy.domain.like.batch

import io.github.oshai.kotlinlogging.KotlinLogging
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.programmers.signalbuddy.domain.feedback.repository.FeedbackRepository
import org.programmers.signalbuddy.domain.like.dto.LikeRequestType
import org.programmers.signalbuddy.domain.like.dto.LikeUpdateRequest
import org.programmers.signalbuddy.domain.like.repository.LikeJdbcRepository
import org.programmers.signalbuddy.domain.like.service.LikeService
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

open class RequestLikeWriter (
    private val feedbackRepository: FeedbackRepository,
    private val likeJdbcRepository: LikeJdbcRepository,
    private val redisTemplate: StringRedisTemplate
) : ItemWriter<LikeUpdateRequest> {

    @Transactional
    override fun write(chunk: Chunk<out LikeUpdateRequest>) {
        log.info {"like job chunk size : ${chunk.size()}" }

        val savedLikeList = ArrayList<LikeUpdateRequest>() // 저장할 좋아요 데이터
        val deletedLikeList = ArrayList<LikeUpdateRequest>() // 삭제할 좋아요 데이터
        val likeKeyList = ArrayList<String>() // Redis에 저장된 좋아요 데이터의 키

        // 요청된 좋아요 개수 및 좋아요 데이터 반영
        for (request in chunk.items) {
            val feedback = feedbackRepository.findByIdOrNull(request.feedbackId)
            if (feedback == null) {
                redisTemplate.delete(LikeService.generateKey(request))
                continue
            }

            if (LikeRequestType.ADD == request.likeRequestType) {
                savedLikeList.add(request)
                feedback.increaseLike()
            } else if (LikeRequestType.CANCEL == request.likeRequestType) {
                deletedLikeList.add(request)
                feedback.decreaseLike()
            }

            likeKeyList.add(LikeService.generateKey(request))
        }

        likeJdbcRepository.saveAllInBatch(savedLikeList)
        likeJdbcRepository.deleteAllByLikeRequestsInBatch(deletedLikeList)
        redisTemplate.delete(likeKeyList)
    }
}
