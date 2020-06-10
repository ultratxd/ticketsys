package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.entities.spotItem.SpotItem
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.SpotItemOrderDetail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SpotItemOrderDetailTransformer : DocTransformer<SpotItemOrderDetail,SpotItemOrderDetailDto> {

    @Autowired
    private lateinit var itemTransformer: DocTransformer<SpotItem, SpotItemDto>

    override fun transform(data: SpotItemOrderDetail): SpotItemOrderDetailDto? {
        val detail = SpotItemOrderDetailDto()
        detail.nums = data.nums
        detail.perNums= data.perNums
        detail.surplus = data.surplus
        if(data.item != null) {
            detail.item = itemTransformer.transform(data.item!!)
        }
        return detail
    }
}