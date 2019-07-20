
package com.cj.ticketsys.cfg.cache

import java.util.*

interface Cache {
    abstract fun <T> get(key: String, valueType: Class<T>): T?

    abstract fun <T> getList(key: String, valueType: Class<T>): Optional<List<T>>

    abstract fun <T> set(key: String, obj: T?, seconds: Long): Boolean

    abstract fun del(key: String): Boolean

    abstract fun dels(keys: Array<String>): Long

    abstract fun <T> gets(keys: Collection<String>, valueType: Class<T>): Collection<T>

    abstract fun <T> hget(key: String, fieldKey: String, valueType: Class<T>): T?

    abstract fun <T> hset(key: String, fieldKey: String, obj: T): Boolean

    abstract fun <T> hgetall(key: String, valueType: Class<T>): List<T>

    abstract fun hdel(key: String, vararg fieldKeys: String): Boolean

    abstract fun <T> hmset(key: String, objs: Map<String, T>?)

    abstract fun incr(key: String, c: Long): Long
}