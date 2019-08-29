package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.dao.SubOrderDao
import com.cj.ticketsys.entities.CardTicket
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class CardTicketDocTransformer : DocTransformer<CardTicket, CardTicketDto> {

    @Autowired
    private lateinit var subOrderDao: SubOrderDao

    @Autowired
    private lateinit var snapshotTransformer: DocTransformer<String, TicketSnapshotDto>

    override fun transform(data: CardTicket): CardTicketDto? {
        val dto = CardTicketDto(data.id)
        dto.orderId = data.orderId
        dto.orderSid = data.orderSubId
        dto.code = data.code
        dto.cardNo = data.cardNo
        dto.buyTime = data.buyTime
        dto.activatedTime = data.activatedTime
        dto.lastActivateTime = data.lastActivateTime
        dto.expireTime = data.expireTime
        if(data.activatedTime != null) {
            dto.state = 1
        }
        //已过期状态
        if(data.expireTime != null && data.expireTime!!.before(Date())) {
            dto.state = 2
        }
        val subOrder = subOrderDao.get(data.orderSubId)
        if(subOrder != null) {
            dto.ticketSnapshot = snapshotTransformer.transform(subOrder.snapshot ?: "")
        }
        return dto
    }
}