package io.hhplus.concertreservationservice.infrastructure.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedissonConfig(
    @Value("\${spring.data.redis.host}") private val redisHost: String,
    @Value("\${spring.data.redis.port}") private val redisPort: Int,
) {
    companion object {
        const val REDISSON_HOST_PREFIX = "redis://"
        const val DEFAULT_POOL_SIZE = 10
        const val MINIMUM_IDLE_SIZE = 2
    }

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer().apply {
            address = "${REDISSON_HOST_PREFIX}$redisHost:$redisPort"
            connectionPoolSize = DEFAULT_POOL_SIZE
            connectionMinimumIdleSize = MINIMUM_IDLE_SIZE
        }
        return Redisson.create(config)
    }
}
