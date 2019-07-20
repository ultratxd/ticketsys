
package com.cj.ticketsys.cfg.cache

import com.alibaba.fastjson.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import redis.clients.jedis.JedisPool
import java.util.*

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class JedisCache : Cache {

    @Autowired
    lateinit var jedisCluster: JedisPool

    @Autowired
    lateinit var serializer: CacheObjectSerializer

    override fun <T> get(key: String, valueType: Class<T>): T? {
        var str: String? = ""
        try {
            jedisCluster.resource.use { jedis -> str = jedis.get(key) }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        return if (str.isNullOrEmpty()) {
            null
        } else serializer.deserialize(str!!, valueType)
    }

    override fun <T> getList(key: String, valueType: Class<T>): Optional<List<T>> {
        var str: String? = ""
        val list = ArrayList<T>()
        try {
            jedisCluster.resource.use { jedis ->
                if (!jedis.exists(key)) {
                    return Optional.empty()
                }
                str = jedis.get(key)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Optional.of(list)
        }

        if (str.isNullOrEmpty()) {
            return Optional.of(emptyList())
        }
        val array = serializer.deserialize(str!!, ArrayList<T>().javaClass)
        try {
            for (an in array) {
                if (an is Int
                    || an is String
                    || an is Double
                    || an is Float
                    || an is Long
                    || an is Boolean
                    || an is Date
                ) {
                    list.add(an)
                } else {
                    val obj = JSONObject.toJavaObject(an as JSONObject, valueType)
                    list.add(obj)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Optional.of(list)
    }

    override fun <T> set(key: String, obj: T?, seconds: Long): Boolean {
        if (null == obj) {
            return false
        }
        val str = serializer.serialize(obj)
        try {
            jedisCluster.resource.use { jedis ->
                return "OK" == jedis.setex(
                    key,
                    java.lang.Long.valueOf(seconds).toInt(),
                    str
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    override fun del(key: String): Boolean {
        try {
            jedisCluster.resource.use { jedis -> return jedis.del(key) == 1L }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    override fun dels(keys: Array<String>): Long {
        var c: Long = 0
        try {
            jedisCluster.resource.use { jedis ->
                for (key in keys) {
                    c += jedis.del(key)!!
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return c
    }

    override fun <T> gets(keys: Collection<String>, valueType: Class<T>): Collection<T> {
        val strs = ArrayList<String>()
        try {
            jedisCluster.resource.use { jedis ->
                for (key in keys) {
                    strs.add(jedis.get(key))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val list = ArrayList<T>()
        for (str in strs) {
            if (str.isEmpty()) {
                continue
            }
            val obj = serializer.deserialize(str, valueType)
            list.add(obj)
        }
        return list
    }

    override fun <T> hget(key: String, fieldKey: String, valueType: Class<T>): T? {
        try {
            jedisCluster.resource.use { jedis ->
                val str = jedis.hget(key, fieldKey)
                if (!str.isNullOrEmpty()) {
                    return serializer.deserialize(str, valueType)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun <T> hset(key: String, fieldKey: String, obj: T): Boolean {
        try {
            jedisCluster.resource.use { jedis ->
                return jedis.hset(
                    key,
                    fieldKey,
                    serializer.serialize(obj as Any)
                ) > 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    override fun <T> hgetall(key: String, valueType: Class<T>): List<T> {
        val list = ArrayList<T>()
        try {
            jedisCluster.resource.use { jedis ->
                val kvs = jedis.hgetAll(key)
                for ((_, value) in kvs) {
                    if (!value.isNullOrEmpty()) {
                        list.add(serializer.deserialize(value, valueType))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return list
    }

    override fun hdel(key: String, vararg fieldKeys: String): Boolean {
        if (fieldKeys.isEmpty()) {
            return false
        }
        try {
            jedisCluster.resource.use { jedis -> return jedis.hdel(key, *fieldKeys) == fieldKeys.size.toLong() }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    override fun <T> hmset(key: String, objs: Map<String, T>?) {
        if (objs == null || objs.size == 0) {
            return
        }
        try {
            jedisCluster.resource.use { jedis ->
                val map = TreeMap<String, String>()
                for ((key1, value) in objs) {
                    map[key1] = serializer.serialize(value as Any)
                }
                jedis.hmset(key, map)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun incr(key: String, c: Long): Long {
        try {
            jedisCluster.resource.use { jedis ->
                return jedis.incrBy(key, c)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

}