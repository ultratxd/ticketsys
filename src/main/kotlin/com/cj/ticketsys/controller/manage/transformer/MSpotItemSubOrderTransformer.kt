package com.cj.ticketsys.controller.manage.transformer

import com.cj.ticketsys.controller.manage.dto.MSpotItemDto
import com.cj.ticketsys.controller.manage.dto.MSpotItemSubOrderDto
import com.cj.ticketsys.dao.ScenicDao
import com.cj.ticketsys.dao.ScenicSpotDao
import com.cj.ticketsys.entities.spotItem.SpotItem
import com.cj.ticketsys.entities.spotItem.SpotItemSubOrder
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.SpotItemSvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MSpotItemSubOrderTransformer : DocTransformer<SpotItemSubOrder, MSpotItemSubOrderDto> {

    @Autowired
    private lateinit var spotItemSvc: SpotItemSvc

    @Autowired
    private lateinit var scenicSpotDao: ScenicSpotDao

    @Autowired
    private lateinit var scenicDao: ScenicDao

    @Autowired
    private lateinit var itemTransformerM: DocTransformer<SpotItem, MSpotItemDto>

    override fun transform(data: SpotItemSubOrder): MSpotItemSubOrderDto? {
        val dto = MSpotItemSubOrderDto()
        dto.id = data.id
        dto.orderId = data.orderId
        dto.itemId = data.itemId
        dto.itemPid = data.itemPid
        dto.price = data.price
        dto.unitPrice = data.unitPrice
        dto.nums = data.nums
        dto.perNums = data.perNums
        dto.createTime = data.createTime
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
            dto.itemM = itemTransformerM.transform(item)!!
        }
        return dto
    }
}