package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.cfg.cache.Cache
import com.cj.ticketsys.svc.IdBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service("TICKET_ID_BUILDER")
class OrderIdBuilder : IdBuilder {
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
        return String.format("$ym%s%06d%03d",tag, i,Random().nextInt(1000))
    }
}

@Service("ITEM_ID_BUILDER")
class ItemIdBuilder : IdBuilder {
    private val SEQ_KEY = "ticketsys:item-seq-id"

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
        return String.format("$ym%s%06d", tag,i)
    }
}