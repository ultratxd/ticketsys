package com.cj.ticketsys.svc.impl

import com.alibaba.fastjson.JSON
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.dao.ScenicSpotDao
import com.cj.ticketsys.dao.SubOrderDao
import com.cj.ticketsys.dao.TicketDao
import com.cj.ticketsys.dao.TicketPriceDao
import com.cj.ticketsys.entities.*
import com.cj.ticketsys.svc.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

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
    private lateinit var subOrderDao: SubOrderDao

    @Autowired
    private lateinit var scenicSpotDao: ScenicSpotDao

    @Autowired
    private lateinit var inventoryManagement: InventoryManagement

    @Autowired
    private lateinit var ticketSnapshotCreator: SnapshotCreator<Ticket, TicketSnapshot>

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
                Utils.intToDate(bt.date),
                order.partner!!.channelType
            ) ?: return BuyResult("BUY:1003", "购买的票不存在")

            val existPrice = ts.prices.stream().filter { t -> t.id == bt.ticketPriceId }.count()
            if (existPrice == 0L) {
                return BuyResult("BUY:1005", "票价不存在")
            }

            val surplus = inventoryManagement.surplus(price.id)
            if (surplus <= 0) {
                return BuyResult("BUY:1006", "票已售罄")
            }

            //超时购买
            if(price.buyTime != null && price.buyTime!! > 0 && bt.date == Utils.dateToYYYYMMDDInt(Date())) {
                if(Utils.zeroToNowSeconds(Date()) > price.buyTime!!) {
                    return BuyResult("BUY:1007", ticket.name + "的当日票已超过购买时间")
                }
            }
            //超过数量
            if(price.buyLimit != null && price.buyLimit!! > 0) {
                val buyThisCount = subOrderDao.idCardAndTicketCount(bt.userCard,ticket.id)
                if(bt.ticketNums + buyThisCount > price.buyLimit!!) {
                    return BuyResult("BUY:1008", "购买" + ticket.name + "已超过个人购买数量")
                }
            }

            val fPrice =  ts.prices.first { a->a.id == bt.ticketPriceId };
            var unitPrice = fPrice.price
            val idCardPrices = price.getTicketIDCardPrices()
            var priceDiscountType = fPrice.discountType

//            //特定日期折扣
//            val cusDatePrices = price.getTicketCustomDataPrices()
//            if (cusDatePrices.any { a -> a.date == bt.date }) {
//                unitPrice = cusDatePrices.first { a -> a.date == bt.date }.price
//                priceDiscountType = PriceDiscountTypes.Date
//            }
            //特定身份证折扣
            if (bt.cardType == CardTypes.IDCard && idCardPrices.any()) {
                for (idp in idCardPrices) {
                    if (bt.userCard.startsWith(idp.idCardPrefix)) {
                        val count = subOrderDao.idCardAndTicketCount(bt.userCard, ticket.id)
                        if (count > 0) {
                            continue
                        }
                        //有购买特惠日期数据 并 使用日期在购买特惠日期中不存在
                        if(idp.limitDates.isEmpty()) {
                            unitPrice = idp.price
                            priceDiscountType = PriceDiscountTypes.IDCard
                            break
                        }
                        if(idp.limitDates.any { a-> a == bt.date }) {
                            unitPrice = idp.price
                            priceDiscountType = PriceDiscountTypes.IDCard
                            break
                        }
                        break
                    }
                }
            }

            totalMoney += unitPrice * bt.ticketNums
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
            subOrder.ticketId = ts.id
            subOrder.ticketPid = bt.ticketPriceId
            subOrder.unitPrice = unitPrice
            subOrder.totalPrice = unitPrice * bt.ticketNums
            subOrder.nums = bt.ticketNums
            subOrder.pernums = ts.perNums
            subOrder.useDate = Utils.intToDate(bt.date)
            subOrder.cid = ts.cid
            subOrder.priceDiscountType = priceDiscountType

            subOrder.snapshot = JSON.toJSONString(ticketSnapshotCreator.create(ts))
            subOrders.add(subOrder)
        }
        val buyOrder = Order()
        buyOrder.orderId = idBuilder.newId("ORDER")
        buyOrder.price = totalMoney
        buyOrder.childs = order.buyTickets.size
        buyOrder.channelId = order.partner!!.id
        buyOrder.channelUid = order.channelUid
        buyOrder.ip = order.buyerIp
        buyOrder.buyType = order.buyType
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