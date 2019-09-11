package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.dao.TicketDao
import com.cj.ticketsys.entities.Ticket
import com.cj.ticketsys.entities.TicketPrice
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TicketDocTransformer : DocTransformer<Ticket,TicketDto> {

    @Autowired
    private lateinit var ticketDao:TicketDao

    @Autowired
    private lateinit var ticketPriceDocTransformer: DocTransformer<TicketPrice, TicketPriceDto>


    override fun transform(data: Ticket): TicketDto? {
        val dto = TicketDto(data.id)
        dto.name = data.name
        dto.pernums = data.perNums
        dto.enterRemark = data.enterRemark
        dto.buyRemark = data.buyRemark
        dto.stocks = data.stocks
        dto.soldCount = data.solds
        dto.state = data.state.value
        dto.cid = data.cid
        dto.iconUrl = data.iconUrl
        dto.title = data.title



        if(data.prices.any()) {
            val priceDtos = data.prices.map { a -> ticketPriceDocTransformer.transform(a)!! }
            dto.prices.addAll(priceDtos)
        }
        val tags = ticketDao.getTags(data.id)
        dto.tags.addAll(tags.map { t->t.name })

        return dto
    }
}