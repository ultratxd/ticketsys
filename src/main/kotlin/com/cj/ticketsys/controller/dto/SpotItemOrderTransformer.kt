package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.dao.ScenicDao
import com.cj.ticketsys.dao.ScenicSpotDao
import com.cj.ticketsys.entities.spotItem.SpotItemOrder
import com.cj.ticketsys.entities.spotItem.SpotItemSubOrder
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.SpotItemSvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SpotItemOrderTransformer : DocTransformer<SpotItemOrder, SpotItemOrderDto> {

    override fun transform(data: SpotItemOrder): SpotItemOrderDto? {
        val dto = SpotItemOrderDto()
        dto.orderId = data.orderId
        dto.createTime = data.createTime
        dto.nums = data.nums
        dto.totalPrice = data.totalPrice
        dto.payTime = data.payTime
        dto.payNo = data.payNo
        dto.refundNo = data.refundNo
        dto.refundTime = data.refundTime

        dto.state = data.state
        dto.priceDiscountTypes = data.priceDiscountTypes

        return dto
    }
}