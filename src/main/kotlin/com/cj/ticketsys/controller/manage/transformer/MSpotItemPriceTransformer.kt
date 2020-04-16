package com.cj.ticketsys.controller.manage.transformer

import com.cj.ticketsys.controller.manage.dto.MSpotItemPriceDto
import com.cj.ticketsys.entities.spotItem.SpotItemPrice
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.stereotype.Component

@Component
class MSpotItemPriceTransformer: DocTransformer<SpotItemPrice, MSpotItemPriceDto> {

    override fun transform(data: SpotItemPrice): MSpotItemPriceDto? {
        val dto = MSpotItemPriceDto()
        dto.id = data.id
        dto.name = data.name
        dto.channelType = data.channelType.code()
        dto.desc = data.desc
        dto.itemId = data.itemId
        dto.solds = data.solds
        dto.state = data.state.code()
        dto.stocks = data.stocks
        dto.unit = data.unit
        dto.price = data.price
        return dto
    }
}