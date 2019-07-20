
package com.cj.ticketsys.cfg.cache

import com.alibaba.fastjson.JSON
import org.springframework.stereotype.Component

@Component
class FastJSONCacheObjectSerializer :CacheObjectSerializer {
    override fun serialize(obj: Any): String {
        return JSON.toJSONString(obj)
    }

    override fun <T> deserialize(str: String, valueType: Class<T>): T {
        return JSON.parseObject(str, valueType)
    }
}