package com.cj.ticketsys.controller.dto

import com.alibaba.fastjson.JSON
import com.cj.ticketsys.entities.SubOrder
import com.cj.ticketsys.entities.Ticket
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.TicketSnapshot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SubOrderDocTransformer : DocTransformer<SubOrder, SubOrderDto> {

    @Autowired
    private lateinit var ticketUseDayTransformer: TicketUseDayTransformer

    override fun transform(data: SubOrder): SubOrderDto? {
        val dto = SubOrderDto(data.id, data.orderId)
        dto.createTime = data.createTime
        dto.payTime = data.payTime
        dto.payNo = data.payNo
        dto.refundTime = data.refundTime
        dto.refundNo = data.refundNo
        dto.userCard = data.uCard
        dto.userMobile = data.uMobile
        dto.uid = data.channelUid
        dto.scenicId = data.scenicId
        dto.scenicSid = data.scenicSid
        dto.ticketId = data.ticketId
        dto.unitPrice = data.unitPrice
        dto.totalPrice = data.totalPrice
        dto.nums = data.nums
        dto.pernums = data.pernums
        dto.state = data.state.value
        dto.useDate = data.useDate
        dto.lastUseDate = data.lastUseDate
        dto.cardType = data.cardType.value
        dto.userCard = data.uCard
        dto.userMobile = data.uMobile
        dto.userName = data.uName
        dto.cid = data.cid

        try {
            val ticket = JSON.parseObject(data.snapshot, TicketSnapshot::class.java)
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
                ssPrice.refundType = tp.refundType.value
                ssPrice.title = tp.title
                ssPrice.noticeRemark = tp.noticeRemark
                ssPrice.remark = tp.remark
                ssPrice.description = tp.description
                snapshot.prices.add(ssPrice)
            }
            dto.ticketSnapshot = snapshot
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return dto
    }
}