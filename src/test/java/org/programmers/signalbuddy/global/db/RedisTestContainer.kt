package org.programmers.signalbuddy.global.db

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.programmers.signalbuddy.global.config.RedisConfig
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@Import(RedisConfig::class)
interface RedisTestContainer {
    companion object {
        private const val REDIS_IMAGE: String = "redis:7.4.1-alpine"
        private const val REDIS_PORT: Int = 6379

        @JvmStatic
        @Container
        val REDIS_CONTAINER: GenericContainer<*> = GenericContainer(DockerImageName.parse(REDIS_IMAGE))
            .withExposedPorts(REDIS_PORT)
            .withReuse(true)

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            if (!REDIS_CONTAINER.isRunning) {
                REDIS_CONTAINER.start()
            }
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            REDIS_CONTAINER.close()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerRedisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost)
            registry.add("spring.data.redis.port", REDIS_CONTAINER::getFirstMappedPort)
        }
    }
}
