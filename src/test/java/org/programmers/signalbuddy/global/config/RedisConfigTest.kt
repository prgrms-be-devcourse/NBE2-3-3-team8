package org.programmers.signalbuddy.global.config

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.programmers.signalbuddy.global.db.RedisTestContainer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
internal class RedisConfigTest : RedisTestContainer {

    @Autowired
    private val redisTemplate: StringRedisTemplate? = null

    companion object {
        private val log: Logger = LoggerFactory.getLogger(RedisConfigTest::class.java)
    }

    @Test
    fun `redis 작동 확인`() {
        // given

        val key = "key"
        val value = "value"
        val valueOperations = redisTemplate!!.opsForValue()
        valueOperations[key] = value

        val storedValue = valueOperations[key]
        Assertions.assertThat(storedValue).isEqualTo(value)

        log.info("stored value: {}", storedValue)
        redisTemplate.delete(key)
    }
}
