package com.cj.ticketsys.controller.manage.transformer

import com.cj.ticketsys.controller.manage.dto.MTicketDto
import com.cj.ticketsys.controller.manage.dto.MTicketPriceDto
import com.cj.ticketsys.controller.manage.dto.TicketOfItemDto
import com.cj.ticketsys.dao.TicketDao
import com.cj.ticketsys.dao.TicketPriceDao
import com.cj.ticketsys.entities.Ticket
import com.cj.ticketsys.entities.TicketPrice
import com.cj.ticketsys.entities.spotItem.TicketOfItem
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MSpotItemOfTicketTransformer : DocTransformer<TicketOfItem, TicketOfItemDto> {

    @Autowired
    private lateinit var ticketDao: TicketDao

    @Autowired
    private lateinit var ticketPriceDao: TicketPriceDao

    @Autowired
    private lateinit var ticketTransformer: DocTransformer<Ticket, MTicketDto>

    @Autowired
    private lateinit var priceTransformer: DocTransformer<TicketPrice, MTicketPriceDto>

    override fun transform(data: TicketOfItem): TicketOfItemDto? {
        val dto = TicketOfItemDto()
        dto.itemId = data.itemId
        dto.itemPriceId = data.itemPriceId
        dto.nums= data.nums
        dto.ticketId = data.ticketId
        dto.ticketPriceId = data.ticketPriceId

        val ticket = ticketDao.get(data.ticketId)
        if(ticket != null) {
            dto.ticket = ticketTransformer.transform(ticket)!!
        }
        val tPrice = ticketPriceDao.get(data.ticketPriceId)
        if(tPrice != null) {
            dto.ticketPrice = priceTransformer.transform(tPrice)!!
        }
        return dto
    }
}