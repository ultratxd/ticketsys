package com.cj.ticketsys.controller.manage.transformer

import com.cj.ticketsys.controller.manage.dto.MScenicSpotDto
import com.cj.ticketsys.controller.manage.dto.MTicketDto
import com.cj.ticketsys.controller.manage.dto.MTicketPriceDto
import com.cj.ticketsys.dao.ScenicSpotDao
import com.cj.ticketsys.dao.TicketDao
import com.cj.ticketsys.entities.ScenicSpot
import com.cj.ticketsys.entities.Ticket
import com.cj.ticketsys.entities.TicketPrice
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MTicketTransformer  : DocTransformer<Ticket, MTicketDto> {

    @Autowired
    private lateinit var ticketDao: TicketDao

    @Autowired
    private lateinit var spotDao: ScenicSpotDao

    @Autowired
    private lateinit var spotTransformer: DocTransformer<ScenicSpot, MScenicSpotDto>

    @Autowired
    private lateinit var mTicketTransformer: DocTransformer<TicketPrice, MTicketPriceDto>

    override fun transform(data: Ticket): MTicketDto? {
        val dto = MTicketDto(data.id)
        dto.cloudId = data.cloudId
        dto.name = data.name
        dto.pernums = data.perNums
        dto.enterRemark = data.enterRemark
        dto.buyRemark = data.buyRemark
        dto.stocks = data.stocks
        dto.soldCount = data.solds
        dto.state = data.state.value
        dto.cid = data.cid
        dto.frontView = data.frontView
        dto.iconUrl = data.iconUrl
        dto.title = data.title
        dto.extra = data.map
        dto.createTime = data.createTime
        dto.displayOrder = data.displayOrder

        if(data.openStartTime != null) {
            dto.openStartTs = data.openStartTime!!.time / 1000L
        }
        if(data.openEndTime != null) {
            dto.openEndTs = data.openEndTime!!.time / 1000L
        }


        if(data.prices.any()) {
            val priceDtos = data.prices.map { a -> mTicketTransformer.transform(a)!! }
            dto.prices.addAll(priceDtos)
        }
        val tags = ticketDao.getTags(data.id)
        dto.tags.addAll(tags.map { t->t.name })

        val relatedSpotIds = ticketDao.getTktRelatedSpotIds(data.id)
        if(relatedSpotIds.any()) {
            for (sid in relatedSpotIds) {
                val spot = spotDao.get(sid)
                if(spot != null) {
                    dto.relatedSpots.add(spotTransformer.transform(spot)!!)
                }
            }
        }

        return dto
    }
}