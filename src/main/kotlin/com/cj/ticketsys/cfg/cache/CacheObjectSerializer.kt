
package com.cj.ticketsys.cfg.cache

interface CacheObjectSerializer {
    abstract fun serialize(obj: Any): String

    abstract fun <T> deserialize(str: String, valueType: Class<T>): T
}