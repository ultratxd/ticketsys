package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.entities.spotItem.SpotItem
import com.cj.ticketsys.entities.spotItem.SpotItemPrice
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.SpotItemSvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SpotItemTransformer: DocTransformer<SpotItem, SpotItemDto> {

    override fun transform(data: SpotItem): SpotItemDto? {
        val dto = SpotItemDto()
        dto.id = data.id
        dto.name = data.name
        dto.desc1 = data.desc1
        dto.desc2 = data.desc2
        dto.price = data.price
        dto.perNums = data.personalNums
        dto.scenicId = data.scenicId
        dto.scenicSpotId = data.scenicSpotId
        return dto
    }
}