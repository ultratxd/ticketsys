package com.cj.ticketsys.controller.dto

import com.alibaba.fastjson.JSON
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.TicketSnapshot
import com.google.common.base.Strings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TicketSnapshotTransformer : DocTransformer<String,TicketSnapshotDto> {

    @Autowired
    private lateinit var ticketUseDayTransformer: TicketUseDayTransformer

    override fun transform(data: String): TicketSnapshotDto? {
        if(Strings.isNullOrEmpty(data)) {
            return null
        }
        try {
            val ticket = JSON.parseObject(data, TicketSnapshot::class.java)
            val snapshot = TicketSnapshotDto()
            snapshot.id = ticket.id
            snapshot.name = ticket.name
            snapshot.perNums = ticket.perNums
            snapshot.iconUrl = ticket.iconUrl
            snapshot.buyRemark = ticket.buyRemark
            snapshot.enterRemark = ticket.enterRemark
            snapshot.tags.addAll(ticket.tags)
            snapshot.categoryName = ticket.categoryName
            snapshot.cid = ticket.cid
            for (tp in ticket.prices) {
                val ssPrice = TicketPriceSnapshotDto()
                ssPrice.id = tp.id
                ssPrice.tid = tp.tid
                ssPrice.useDate = if(tp.useDate != null) ticketUseDayTransformer.transform(tp.useDate!!) else null
                ssPrice.channelType = tp.channelType.value
                ssPrice.name = tp.name
                ssPrice.price = tp.price
                ssPrice.originalPrice = tp.originalPrice
                ssPrice.refundType = tp.refundType.value
                ssPrice.title = tp.title
                ssPrice.noticeRemark = tp.noticeRemark
                ssPrice.remark = tp.remark
                ssPrice.description = tp.description
                snapshot.prices.add(ssPrice)
            }
            return snapshot
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}