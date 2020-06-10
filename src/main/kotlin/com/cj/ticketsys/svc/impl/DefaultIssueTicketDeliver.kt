package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.dao.*
import com.cj.ticketsys.entities.*
import com.cj.ticketsys.svc.IssueTicketDeliver
import com.cj.ticketsys.svc.Utils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception
import java.util.*

@Service("Def_Issue_Ticket_Deliver")
class DefaultIssueTicketDeliver : IssueTicketDeliver {

    @Autowired
    private lateinit var orderDao: OrderDao

    @Autowired
    private lateinit var subOrderDao: SubOrderDao

    @Autowired
    private lateinit var partnerDao: PartnerDao

    @Autowired
    private lateinit var orderTicketCodeDao: OrderTicketCodeDao

    @Autowired
    private lateinit var cardTicketDao: CardTicketDao

    @Transactional(rollbackFor = [Exception::class])
    override fun issue(orderNo: String) {
        val order = orderDao.get(orderNo) ?: return
        val subOrders = subOrderDao.gets(orderNo)
        val partner = partnerDao.get(order.channelId)
        //普通票
        if (subOrders.any { a -> a.cid != TicketCategories.Card.value }) {
            val tCode = OrderTicketCode()
            tCode.orderId = orderNo
            tCode.nums = subOrders.sumBy { a -> a.nums }
            tCode.state = TicketCodeStates.Unused
            tCode.createTime = Date()
            tCode.provider = OrderTicketCodeProviders.System
            val first = subOrders.first { a -> a.cid != TicketCategories.Card.value }
            tCode.useDate = first.useDate!!
            tCode.code = makeCode(partner?.channelType, first.createTime, first.cid)

            val c = orderTicketCodeDao.insert(tCode)
            if (c > 0) {

                subOrders.map { s ->
                    {
                        s.state = OrderStates.Issued
                        s.issueTicketTime = Date()
                        subOrderDao.update(s)
                    }
                }
            }
        }

        //年票
        if (subOrders.any { a -> a.cid == TicketCategories.Card.value }) {
            for (subOrder in subOrders) {
                if (subOrder.cid == TicketCategories.Card.value) {
                    val cTicket = CardTicket()
                    cTicket.orderId = subOrder.orderId
                    cTicket.orderSubId = subOrder.id
                    cTicket.channelId = subOrder.channelId
                    cTicket.channelUid = subOrder.channelUid
                    cTicket.buyTime = subOrder.createTime
                    cTicket.lastActivateTime = Date(subOrder.createTime.time + (1000L * 3600 * 24 * 300))
                    cTicket.code = makeCode(partner?.channelType, subOrder.createTime, TicketCategories.Card.value)
                    cardTicketDao.insert(cTicket)
                    subOrder.state = OrderStates.Issued
                    subOrder.issueTicketTime = Date()
                    subOrderDao.update(subOrder)
                }
            }
        }

        order.state = OrderStates.Issued
        order.issueTicketTime = Date()
        orderDao.update(order)
    }

    fun makeCode(channelType: ChannelTypes?, date: Date, cid: Int): String {
//        val datefmt = SimpleDateFormat("yyMMdd")
        //需要加入年卡激活，使用前两位来区分散票或年卡类型
        val ydm = Utils.dateZoneFormat(date, "MMdd")//datefmt.format(date)
        val tag = String.format("%02d", channelType?.value ?: 99)
        val rnd = String.format("%05d",Random().nextInt(100000))
        return "A$tag$cid$ydm$rnd"
    }
}