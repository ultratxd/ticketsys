package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.controller.manage.dto.SpotItemDto
import com.cj.ticketsys.dao.TicketUseDateDao
import com.cj.ticketsys.entities.TicketPrice
import com.cj.ticketsys.entities.TicketUseDate
import com.cj.ticketsys.entities.spotItem.SpotItem
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.SpotItemSvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TicketPriceTransformer: DocTransformer<TicketPrice,TicketPriceDto> {

    @Autowired
    private lateinit var useDateDao: TicketUseDateDao

    @Autowired
    private lateinit var useDateTransformer:DocTransformer<TicketUseDate,TicketUseDateDto>

    @Autowired
    private lateinit var spotItemSvc: SpotItemSvc

    @Autowired
    private lateinit var spotItemTransformer: DocTransformer<SpotItem, SpotItemDto>

    override fun transform(data: TicketPrice): TicketPriceDto? {
        val dto = TicketPriceDto(data.id)
        dto.name = data.name
        dto.tid = data.tid
        dto.channelType = data.channelType.value
        dto.price = data.price
        dto.originalPrice = data.originalPrice ?: 0.0
        dto.stocks = data.stocks
        dto.soldCount = data.solds
        dto.state = data.state.value
        dto.refundType = data.refundType.value
        dto.title = data.title
        dto.noticeRemark = data.noticeRemark
        dto.remark = data.remark
        dto.description = data.description
        dto.buyLimit = data.buyLimit
        dto.buyTime = data.buyTime

        val useDate = useDateDao.get(data.useDateId)
        if(useDate != null) {
            dto.useDate = useDateTransformer.transform(useDate)!!
        }

        /**
         * 赠送小项目
         */
        val items = spotItemSvc.queryTicketItems(data.id)
        val itemDtos = ArrayList<SpotItemDto>()
        for(item in items) {
            itemDtos.add(spotItemTransformer.transform(item)!!)
        }
        dto.items = itemDtos

        return dto
    }
}