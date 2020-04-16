package com.cj.ticketsys.controller.manage.transformer

import com.cj.ticketsys.controller.manage.dto.MSpotItemOrderDto
import com.cj.ticketsys.controller.manage.dto.MSpotItemSubOrderDto
import com.cj.ticketsys.dao.ScenicDao
import com.cj.ticketsys.dao.ScenicSpotDao
import com.cj.ticketsys.entities.spotItem.SpotItemOrder
import com.cj.ticketsys.entities.spotItem.SpotItemSubOrder
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.SpotItemSvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MSpotItemOrderTransformer : DocTransformer<SpotItemOrder, MSpotItemOrderDto> {

    @Autowired
    private lateinit var spotItemSvc: SpotItemSvc

    @Autowired
    private lateinit var subOrderTransformerM: DocTransformer<SpotItemSubOrder, MSpotItemSubOrderDto>

    override fun transform(data: SpotItemOrder): MSpotItemOrderDto? {
        val dto = MSpotItemOrderDto()
        dto.orderId = data.orderId
        dto.createTime = data.createTime
        dto.nums = data.nums
        dto.totalPrice = data.totalPrice
        dto.payTime = data.payTime
        dto.payNo = data.payNo
        dto.refundNo = data.refundNo
        dto.refundTime = data.refundTime
        dto.state = data.state.code()
        dto.channelId = data.channelId
        dto.channelUid = data.channelUid
        dto.code = data.code
        dto.priceDiscountTypes = data.priceDiscountTypes

        val subOrders = spotItemSvc.querySubOrders(data.orderId)
        val subList = ArrayList<MSpotItemSubOrderDto>()
        for (sOrder in subOrders) {
            subList.add(subOrderTransformerM.transform(sOrder)!!)
        }
        dto.subOrderMS = subList

        return dto
    }
}