package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.cfg.cache.Cache
import com.cj.ticketsys.svc.IdBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class RedisIdBuilder : IdBuilder {
    private val SEQ_KEY = "ticketsys:seq-id"

    @Autowired
    private lateinit var cache: Cache

    override fun newId(tag: String): String {
        var i = 0L
        try {
            i = cache.incr(SEQ_KEY, 1)

        } catch (e: Exception) {
            e.printStackTrace()
            if (i <= 0) {
                //6位
                i = (Math.random() * 1000000).toLong()
            }
        }
        val sdf = SimpleDateFormat("yyyyMM")
        val ym = sdf.format(Date())
        return String.format("$ym%06d%05d", i,Random().nextInt(100000))
    }
}