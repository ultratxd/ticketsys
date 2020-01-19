package com.cj.ticketsys.controller.clientapi.transformer

import com.cj.ticketsys.controller.clientapi.dto.ClientSubOrderDto
import com.cj.ticketsys.entities.ClientSubOrder
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.stereotype.Component

@Component
class ClientSubOrderTransformer : DocTransformer<ClientSubOrder, ClientSubOrderDto> {

    override fun transform(data: ClientSubOrder): ClientSubOrderDto? {
        val dto = ClientSubOrderDto()
        dto.id = data.id
        dto.clientId = data.clientId
        dto.cloudId = data.cloudId
        dto.clientOrderNo = data.clientOrderNo
        dto.orderType = data.orderType
        dto.ticketId = data.ticketId
        dto.ticketName = data.ticketName
        dto.amount = data.amount
        dto.unitPrice = data.unitPrice
        dto.nums = data.nums
        dto.perNums = data.perNums
        dto.createTime = data.createTime
        dto.prints = data.prints
        dto.useDate = data.useDate
        dto.enterTime = data.enterTime
        dto.clientParentId = data.clientParentId
        return dto
    }
}