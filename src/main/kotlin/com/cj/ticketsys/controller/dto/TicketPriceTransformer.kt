package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.dao.TicketUseDateDao
import com.cj.ticketsys.entities.TicketPrice
import com.cj.ticketsys.entities.TicketUseDate
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TicketPriceTransformer: DocTransformer<TicketPrice,TicketPriceDto> {

    @Autowired
    private lateinit var useDateDao: TicketUseDateDao

    @Autowired
    private lateinit var useDateTransformer:DocTransformer<TicketUseDate,TicketUseDateDto>

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

        val useDate = useDateDao.get(data.useDateId)
        if(useDate != null) {
            dto.useDate = useDateTransformer.transform(useDate)!!
        }

        return dto
    }
}