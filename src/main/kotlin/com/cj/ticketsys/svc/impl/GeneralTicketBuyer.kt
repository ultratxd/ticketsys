package com.cj.ticketsys.svc.impl

import com.alibaba.fastjson.JSON
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.dao.ScenicSpotDao
import com.cj.ticketsys.dao.TicketDao
import com.cj.ticketsys.dao.TicketPriceDao
import com.cj.ticketsys.entities.CardTypes
import com.cj.ticketsys.entities.Order
import com.cj.ticketsys.entities.SubOrder
import com.cj.ticketsys.svc.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception

@Service
class GeneralTicketBuyer : TicketBuyer {

    @Autowired
    private lateinit var ticketSvc: TicketSvc

    @Autowired
    private lateinit var priceDao: TicketPriceDao

    @Autowired
    private lateinit var ticketDao: TicketDao

    @Autowired
    private lateinit var idBuilder: IdBuilder

    @Autowired
    private lateinit var orderSvc: OrderSvc

    @Autowired
    private lateinit var scenicSpotDao: ScenicSpotDao

    @Autowired
    private lateinit var inventoryManagement: InventoryManagement

    @Transactional(rollbackFor = [Exception::class])
    @Synchronized
    override fun buy(order: BuyTicketOrder): BuyResult {
        if (order.partner == null) {
            return BuyResult("BUY:1002", "商户未设置")
        }

        var totalMoney = 0.0
        var totalCount = 0
        val subOrders = ArrayList<SubOrder>()
        for (bt in order.buyTickets) {
            val price = priceDao.get(bt.ticketPriceId) ?: return BuyResult("BUY:1002", "购买的票价不存在")
            val ticket = ticketDao.get(price.tid) ?: return BuyResult("BUY:1003", "购买的票不存在")
            val scenic = scenicSpotDao.get(order.scenicSpotId) ?: return BuyResult("BUY:1004", "景点不存在")
            val ts = ticketSvc.getTicket(
                ticket.id,
                order.scenicSpotId,
                Utils.intToDate(bt.date),
                order.partner!!.channelType
            )
                ?: return BuyResult("BUY:1003", "购买的票不存在")

            val existPrice = ts.prices.stream().filter { t -> t.id == price.id }.count()
            if (existPrice == 0L) {
                return BuyResult("BUY:1005", "票价不存在")
            }

            val surplus = inventoryManagement.surplus(price.id)
            if (surplus <= 0) {
                return BuyResult("BUY:1006", "票已售罄")
            }

            totalMoney += price.price * bt.ticketNums
            totalCount += bt.ticketNums

            val subOrder = SubOrder()
            subOrder.channelId = order.partner!!.id
            subOrder.channelUid = order.channelUid
            subOrder.uName = bt.userName
            subOrder.cardType = bt.cardType
            subOrder.uCard = bt.userCard
            subOrder.uMobile = bt.userMobile
            subOrder.scenicId = scenic.pid
            subOrder.scenicSid = scenic.id
            subOrder.ticketId = ticket.id
            subOrder.ticketPid = bt.ticketPriceId
            subOrder.unitPrice = price.price
            subOrder.totalPrice = price.price * bt.ticketNums
            subOrder.nums = bt.ticketNums
            subOrder.pernums = ticket.perNums
            subOrder.useDate = Utils.intToDate(bt.date)
            subOrder.cid = ticket.cid

            ticket.prices.add(price)
            subOrder.snapshot = JSON.toJSONString(ticket)
            subOrders.add(subOrder)
        }
        val buyOrder = Order()
        buyOrder.orderId = idBuilder.newId("ORDER")
        buyOrder.price = totalMoney
        buyOrder.childs = order.buyTickets.size
        buyOrder.channelId = order.partner!!.id
        buyOrder.channelUid = order.channelUid
        buyOrder.ip = order.buyerIp
        subOrders.map { a -> a.orderId = buyOrder.orderId }
        val ok = orderSvc.create(buyOrder, subOrders)
        if (ok) {
            val result = BuyResult(RESULT_SUCCESS, "ok")
            result.order = buyOrder
            return result
        }
        return BuyResult("fail", "创建订单失败")
    }
}