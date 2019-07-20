
package com.cj.ticketsys.cfg

import com.google.common.base.Strings
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

@Configuration
@Slf4j
class JedisConfiguration {

    @Autowired
    lateinit var jedisProperties: JedisProperties

    @Bean
    fun jedisPoolConfig(): JedisPoolConfig {
        val jedisPoolConfig = JedisPoolConfig()
        jedisPoolConfig.setMaxTotal(jedisProperties.jedis.pool.maxActive)
        jedisPoolConfig.setMaxIdle(jedisProperties.jedis.pool.maxIdle)
        jedisPoolConfig.setMinIdle(jedisProperties.jedis.pool.minIdle)
        jedisPoolConfig.setMaxWaitMillis(jedisProperties.jedis.pool.maxWait.toMillis())
        jedisPoolConfig.setTestOnBorrow(true)
        jedisPoolConfig.setTestOnReturn(true)
        return jedisPoolConfig
    }

    @Bean
    fun jedisPool(config: JedisPoolConfig): JedisPool {
        if(Strings.isNullOrEmpty(jedisProperties.password)) {
            return JedisPool(
                config,
                jedisProperties.host,
                jedisProperties.port,
                jedisProperties.timeout.toMillis().toInt()
            )
        }
        return JedisPool(
            config,
            jedisProperties.host,
            jedisProperties.port,
            jedisProperties.timeout.toMillis().toInt(),
            jedisProperties.password
        )
    }

}