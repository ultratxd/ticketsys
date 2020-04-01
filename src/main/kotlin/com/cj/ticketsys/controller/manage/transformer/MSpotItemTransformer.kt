package com.cj.ticketsys.controller.manage.transformer

import com.cj.ticketsys.controller.manage.dto.SpotItemDto
import com.cj.ticketsys.controller.manage.dto.SpotItemPriceDto
import com.cj.ticketsys.entities.spotItem.SpotItem
import com.cj.ticketsys.entities.spotItem.SpotItemPrice
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.SpotItemSvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MSpotItemTransformer: DocTransformer<SpotItem, SpotItemDto> {

    @Autowired
    private lateinit var spotItemSvc: SpotItemSvc

    @Autowired
    private lateinit var spotItemPriceTransformer: DocTransformer<SpotItemPrice, SpotItemPriceDto>

    override fun transform(data: SpotItem): SpotItemDto? {
        val dto = SpotItemDto()
        dto.id = data.id
        dto.name = data.name
        dto.desc1 = data.desc1
        dto.desc2 = data.desc2
        dto.price = data.price
        dto.personalNums = data.personalNums
        dto.scenicId = data.scenicId
        dto.scenicSpotId = data.scenicSpotId
        dto.enabled = data.enabled

        val prices = spotItemSvc.querySpotItemPrices(data.id)
        val dtos = ArrayList<SpotItemPriceDto>()
        for(price in prices) {
            val dto = spotItemPriceTransformer.transform(price)!!
            dtos.add(dto)
        }
        dto.prices = dtos
        return dto
    }
}