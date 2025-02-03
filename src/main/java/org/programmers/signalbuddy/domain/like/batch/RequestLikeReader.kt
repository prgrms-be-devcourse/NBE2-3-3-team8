package org.programmers.signalbuddy.domain.like.batch

import org.programmers.signalbuddy.domain.like.dto.LikeUpdateRequest
import org.programmers.signalbuddy.domain.like.service.LikeService
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.ItemStreamReader
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.Cursor
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets

@Transactional(readOnly = true)
class RequestLikeReader (
    private val redisTemplate: StringRedisTemplate
) : ItemStreamReader<LikeUpdateRequest?> {

    private lateinit var cursor: Cursor<ByteArray>

    override fun open(executionContext: ExecutionContext) {
        val scanOptions = ScanOptions.scanOptions()
            .match(LikeService.likeKeyPrefix + "*")
            .build()

        cursor = redisTemplate.executeWithStickyConnection { connection: RedisConnection ->
            connection.scan(scanOptions)
        }
    }

    @Throws(Exception::class)
    override fun read(): LikeUpdateRequest? {
        if (cursor.hasNext()) {
            val key = String(cursor.next(), StandardCharsets.UTF_8)
            val keyInfo = key.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val feedbackId = keyInfo[1].toLong()
            val memberId = keyInfo[2].toLong()
            val likeRequestType = redisTemplate.opsForValue()[key]

            return LikeUpdateRequest(
                feedbackId = feedbackId,
                memberId = memberId,
                likeRequestType = likeRequestType!!
            )
        }

        return null
    }
}
