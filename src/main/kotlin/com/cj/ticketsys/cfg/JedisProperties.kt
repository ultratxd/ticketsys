
package com.cj.ticketsys.cfg

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
@ConfigurationProperties(prefix = "spring.redis")
class JedisProperties : RedisProperties() {
    var maxAttempt:Int = 5
    var soTimeout: Int = 3000
}