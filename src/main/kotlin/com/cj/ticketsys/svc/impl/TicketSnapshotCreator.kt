package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.dao.TicketCategories
import com.cj.ticketsys.dao.TicketDao
import com.cj.ticketsys.dao.TicketUseDateDao
import com.cj.ticketsys.entities.Ticket
import com.cj.ticketsys.svc.SnapshotCreator
import com.cj.ticketsys.svc.TicketPriceSnapshot
import com.cj.ticketsys.svc.TicketSnapshot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TicketSnapshotCreator : SnapshotCreator<Ticket, TicketSnapshot> {

    @Autowired
    private lateinit var ticketDao: TicketDao

    @Autowired
    private lateinit var ticketUseDateDao: TicketUseDateDao

    override fun create(obj: Ticket): TicketSnapshot {
        val snapshot = TicketSnapshot()
        snapshot.id = obj.id
        snapshot.name = obj.name
        snapshot.title = obj.title
        snapshot.perNums = obj.perNums
        snapshot.createTime = obj.createTime
        snapshot.enterRemark = obj.enterRemark
        snapshot.buyRemark = obj.buyRemark
        snapshot.stocks = obj.stocks
        snapshot.state = obj.state
        snapshot.cid = obj.cid
        snapshot.categoryName = TicketCategories.getName(obj.cid)
        snapshot.iconUrl = obj.iconUrl
        val tags = ticketDao.getTags(obj.id)
        snapshot.tags.addAll(tags.map { t -> t.name })

        for (price in obj.prices) {
            val ssPrice = TicketPriceSnapshot()
            ssPrice.id = price.id
            ssPrice.tid = price.tid
            ssPrice.useDate = ticketUseDateDao.get(price.useDateId)
            ssPrice.channelType = price.channelType
            ssPrice.name = price.name
            ssPrice.price = price.price
            ssPrice.createTime = price.createTime
            ssPrice.stocks = price.stocks
            ssPrice.stockLimitType = price.stockLimitType
            ssPrice.state = price.state
            ssPrice.refundType = price.refundType
            ssPrice.title = price.title
            ssPrice.remark = price.remark
            ssPrice.description = price.description
            ssPrice.noticeRemark = price.noticeRemark
            snapshot.prices.add(ssPrice)
        }
        return snapshot
    }
}