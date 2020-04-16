package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.entities.spotItem.SpotItem
import com.cj.ticketsys.entities.spotItem.SpotItemSubOrder
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.SpotItemSvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SpotItemSubOrderTransformer : DocTransformer<SpotItemSubOrder, SpotItemSubOrderDto> {

    @Autowired
    private lateinit var spotItemSvc: SpotItemSvc

    @Autowired
    private lateinit var itemTransformer: DocTransformer<SpotItem, SpotItemDto>

    override fun transform(data: SpotItemSubOrder): SpotItemSubOrderDto? {
        val dto = SpotItemSubOrderDto()
        dto.id = data.id
        dto.orderId = data.orderId
        dto.itemId = data.itemId
        dto.itemPid = data.itemPid
        dto.price = data.price
        dto.unitPrice = data.unitPrice
        dto.nums = data.nums
        dto.perNums = data.perNums
        dto.used = data.used

        val item = spotItemSvc.getSpotItem(data.itemId)
        if(item != null) {
            dto.itemM = itemTransformer.transform(item)!!
        }
        return dto
    }
}