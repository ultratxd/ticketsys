package com.cj.ticketsys.controller.manage.transformer

import com.alibaba.fastjson.JSON
import com.cj.ticketsys.controller.dto.TicketPriceSnapshotDto
import com.cj.ticketsys.controller.dto.TicketSnapshotDto
import com.cj.ticketsys.controller.dto.TicketUseDayTransformer
import com.cj.ticketsys.controller.manage.dto.MSubOrderDto
import com.cj.ticketsys.dao.TicketCategories
import com.cj.ticketsys.dao.TicketDao
import com.cj.ticketsys.entities.SubOrder
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.TicketSnapshot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MSubOrderDocTransformer : DocTransformer<SubOrder, MSubOrderDto> {

    @Autowired
    private lateinit var ticketUseDayTransformer: TicketUseDayTransformer

    @Autowired
    private lateinit var ticketDao:TicketDao

    override fun transform(data: SubOrder): MSubOrderDto? {
        val dto = MSubOrderDto(data.id, data.orderId)
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
        dto.categoryName = TicketCategories.getName(data.cid)
        dto.priceDiscountType = data.priceDiscountType.value

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