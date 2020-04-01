package com.cj.ticketsys.controller.manage.transformer

import com.cj.ticketsys.controller.manage.dto.SpotItemDto
import com.cj.ticketsys.controller.manage.dto.SpotItemSubOrderDto
import com.cj.ticketsys.dao.SpotItemDao
import com.cj.ticketsys.entities.spotItem.SpotItem
import com.cj.ticketsys.entities.spotItem.SpotItemSubOrder
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.SpotItemSvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MSpotItemSubOrderTransformer : DocTransformer<SpotItemSubOrder, SpotItemSubOrderDto> {

    @Autowired
    private lateinit var spotItemSvc: SpotItemSvc

    @Autowired
    private lateinit var itemTransformer: DocTransformer<SpotItem, SpotItemDto>

    override fun transform(data: SpotItemSubOrder): SpotItemSubOrderDto? {
        val dto = SpotItemSubOrderDto()
        dto.id = data.id
        dto.orderId = data.orderId
        dto.itemId = data.itemId
        dto.price = data.price
        dto.unitPrice = data.unitPrice
        dto.nums = data.nums
        dto.createTime = data.createTime
        dto.used = data.used

        val item = spotItemSvc.getSpotItem(data.itemId)
        if(item != null) {
            dto.item = itemTransformer.transform(item)!!
        }
        return dto
    }
}