package com.cj.ticketsys.controller.manage.transformer

import com.cj.ticketsys.controller.manage.dto.MSpotItemDto
import com.cj.ticketsys.controller.manage.dto.MSpotItemPriceDto
import com.cj.ticketsys.dao.ScenicSpotDao
import com.cj.ticketsys.entities.spotItem.SpotItem
import com.cj.ticketsys.entities.spotItem.SpotItemPrice
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.SpotItemSvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MSpotItemTransformer: DocTransformer<SpotItem, MSpotItemDto> {

    @Autowired
    private lateinit var spotItemSvc: SpotItemSvc

    @Autowired
    private lateinit var scenicSpotDao: ScenicSpotDao

    @Autowired
    private lateinit var MSpotItemPriceTransformer: DocTransformer<SpotItemPrice, MSpotItemPriceDto>

    override fun transform(data: SpotItem): MSpotItemDto? {
        val dto = MSpotItemDto()
        dto.id = data.id
        dto.name = data.name
        dto.desc1 = data.desc1
        dto.desc2 = data.desc2
        dto.price = data.price
        dto.perNums = data.personalNums
        dto.scenicId = data.scenicId
        dto.scenicSpotId = data.scenicSpotId
        dto.enabled = data.enabled

        val prices = spotItemSvc.querySpotItemPrices(data.id)
        val dtos = ArrayList<MSpotItemPriceDto>()
        for(price in prices) {
            val dto = MSpotItemPriceTransformer.transform(price)!!
            dtos.add(dto)
        }
        dto.priceMS = dtos

        val spot = scenicSpotDao.get(data.scenicSpotId)
        if(spot != null) {
            dto.scenicSpotName = spot.name
        }

        return dto
    }
}