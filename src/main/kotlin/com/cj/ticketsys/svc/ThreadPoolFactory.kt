package com.cj.ticketsys.svc

import com.google.common.util.concurrent.ThreadFactoryBuilder
import java.util.concurrent.*

object ThreadPoolFactory {
    private val THREAD_POOLS = ConcurrentHashMap<String, ThreadPoolExecutor>()

    @Synchronized
    fun newPool(
        poolName: String,
        coolPoolSize: Int,
        maxPoolSize: Int,
        keepAliveTimeMilliseconds: Long,
        queueSize: Int
    ): ThreadPoolExecutor {
        if (THREAD_POOLS.containsKey(poolName)) {
            return THREAD_POOLS[poolName]!!
        }
        val namedThreadFactory = ThreadFactoryBuilder().setNameFormat(poolName).build()
        val pool = ThreadPoolExecutor(
            coolPoolSize, maxPoolSize,
            keepAliveTimeMilliseconds, TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(queueSize), namedThreadFactory, ThreadPoolExecutor.AbortPolicy()
        )
        THREAD_POOLS[poolName] = pool
        return pool
    }
}