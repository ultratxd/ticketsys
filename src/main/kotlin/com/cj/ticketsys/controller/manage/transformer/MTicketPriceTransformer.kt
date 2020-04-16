package com.cj.ticketsys.controller.manage.transformer

import com.cj.ticketsys.controller.dto.TicketUseDateDto
import com.cj.ticketsys.controller.manage.dto.MTicketPriceDto
import com.cj.ticketsys.controller.manage.dto.MSpotItemDto
import com.cj.ticketsys.dao.TicketUseDateDao
import com.cj.ticketsys.entities.TicketPrice
import com.cj.ticketsys.entities.TicketUseDate
import com.cj.ticketsys.entities.spotItem.SpotItem
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.SpotItemSvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MTicketPriceTransformer  : DocTransformer<TicketPrice, MTicketPriceDto> {

    @Autowired
    private lateinit var useDateTransformer:DocTransformer<TicketUseDate, TicketUseDateDto>

    @Autowired
    private lateinit var useDateDao: TicketUseDateDao

    @Autowired
    private lateinit var spotItemSvc: SpotItemSvc

    @Autowired
    private lateinit var MSpotItemTransformer: DocTransformer<SpotItem, MSpotItemDto>

    override fun transform(data: TicketPrice): MTicketPriceDto? {
        val dto = MTicketPriceDto(data.id)
        dto.tid = data.tid
        dto.channelType = data.channelType.value
        dto.name = data.name
        dto.price = data.price
        dto.originalPrice = data.originalPrice ?: 0.0
        dto.stocks = data.stocks
        dto.soldCount = data.solds
        dto.state = data.state.value
        dto.refundType = data.refundType.value
        dto.title = data.title
        dto.noticeRemark = data.noticeRemark
        dto.remark = data.remark
        dto.frontView = data.frontView
        dto.description = data.description
        dto.extra = data.map
        dto.createTime = data.createTime
        dto.stockLimitType = data.stockLimitType.value
        dto.customPrices = data.customPrices
        dto.idCardPrices = data.idCardPrices
        dto.buyLimit = data.buyLimit
        dto.buyTime = data.buyTime
        dto.b2bPLU = data.b2bPLU


        val useDate = useDateDao.get(data.useDateId)
        if (useDate != null) {
            dto.useDate = useDateTransformer.transform(useDate)!!
        }

        /**
         * 赠送小项目
         */
//        val items = spotItemSvc.queryTicketItems(data.id)
//        val itemDtos = ArrayList<MSpotItemDto>()
//        for(item in items) {
//            itemDtos.add(MSpotItemTransformer.transform(item)!!)
//        }
//        dto.itemMS = itemDtos

        return dto
    }
}