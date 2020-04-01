package com.cj.ticketsys.controller.manage.transformer

import com.cj.ticketsys.controller.manage.dto.SpotItemPriceDto
import com.cj.ticketsys.entities.spotItem.SpotItemPrice
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.stereotype.Component

@Component
class MSpotItemPriceTransformer: DocTransformer<SpotItemPrice, SpotItemPriceDto> {

    override fun transform(data: SpotItemPrice): SpotItemPriceDto? {
        val dto = SpotItemPriceDto()
        dto.id = data.id
        dto.name = data.name
        dto.channelType = data.channelType
        dto.desc = data.desc
        dto.itemId = data.itemId
        dto.solds = data.solds
        dto.state = data.state
        dto.stocks = data.stocks
        dto.unit = data.unit
        return dto
    }
}