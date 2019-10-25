package com.cj.ticketsys.svc

import com.cj.ticketsys.dao.TicketPriceDao
import com.cj.ticketsys.entities.ChannelTypes
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class CtripScheduler {
    @Autowired
    private lateinit var tktPriceDao:TicketPriceDao

    //@Scheduled(fixedRate = (1000 * 60 * 3).toLong(), initialDelay = 1000)
    fun pushPrices() {
        val prices = tktPriceDao.getsByChannelAndEnabled(ChannelTypes.Ctrip.code())
        for (price in prices) {

        }
    }
}