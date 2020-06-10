package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.dao.ScenicDao
import com.cj.ticketsys.dao.ScenicSpotDao
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

    @Autowired
    private lateinit var scenicSpotDao: ScenicSpotDao

    @Autowired
    private lateinit var scenicDao: ScenicDao

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
        dto.scenicId = data.scenicId
        dto.scenicSpotId = data.scenicSpotId

        val scenic = scenicDao.get(data.scenicId)
        val scenicSpot = scenicSpotDao.get(data.scenicSpotId)
        if(scenic != null) {
            dto.scenicName =  scenic.name
        }
        if(scenicSpot != null) {
            dto.scenicSpotName = scenicSpot.name
        }


        val item = spotItemSvc.getSpotItem(data.itemId)
        if(item != null) {
            dto.itemM = itemTransformer.transform(item)!!
        }
        return dto
    }
}