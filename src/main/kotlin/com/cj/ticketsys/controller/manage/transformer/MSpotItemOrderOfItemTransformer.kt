package com.cj.ticketsys.controller.manage.transformer

import com.cj.ticketsys.controller.manage.dto.MSpotItemDto
import com.cj.ticketsys.controller.manage.dto.TicketOrderSpotItemDto
import com.cj.ticketsys.entities.spotItem.SpotItem
import com.cj.ticketsys.entities.spotItem.TicketOrderItem
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.SpotItemSvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MSpotItemOrderOfItemTransformer: DocTransformer<TicketOrderItem, TicketOrderSpotItemDto> {

    @Autowired
    private lateinit var spotItemSvc: SpotItemSvc

    @Autowired
    private lateinit var  itemTransformerM: DocTransformer<SpotItem, MSpotItemDto>

    override fun transform(data: TicketOrderItem): TicketOrderSpotItemDto? {
        val dto = TicketOrderSpotItemDto()
        dto.id = data.id
        dto.itemId = data.itemId
        dto.nums = data.nums
        dto.orderId = data.orderId
        dto.orderSubId = data.orderSubId
        val item = spotItemSvc.getSpotItem(data.itemId)
        if(item != null) {
            dto.itemM = itemTransformerM.transform(item)
        }
        return dto
    }
}