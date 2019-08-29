package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.entities.SubOrder
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SubOrderDocTransformer : DocTransformer<SubOrder, SubOrderDto> {

    @Autowired
    private lateinit var snapshotTransformer: DocTransformer<String, TicketSnapshotDto>

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
        dto.priceDiscountType = data.priceDiscountType.value

        dto.ticketSnapshot = snapshotTransformer.transform(data.snapshot ?: "")
        return dto
    }
}