package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.entities.spotItem.SpotItemPrice
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.stereotype.Component

@Component
class SpotItemPriceTransformer: DocTransformer<SpotItemPrice, SpotItemPriceDto> {

    override fun transform(data: SpotItemPrice): SpotItemPriceDto? {
        val dto = SpotItemPriceDto()
        dto.id = data.id
        dto.name = data.name
        dto.channelType = data.channelType.value
        dto.desc = data.desc
        dto.itemId = data.itemId
        dto.solds = data.solds
        dto.state = data.state
        dto.stocks = data.stocks
        dto.unit = data.unit
        dto.price = data.price
        return dto
    }
}