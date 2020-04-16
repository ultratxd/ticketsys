package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.dao.ScenicSpotDao
import com.cj.ticketsys.entities.spotItem.SpotItemOrder
import com.cj.ticketsys.entities.spotItem.SpotItemSubOrder
import com.cj.ticketsys.svc.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

@Service
class GeneralItemBuyer: ItemBuyer {

    @Autowired
    @Qualifier("ITEM_ID_BUILDER")
    private lateinit var idBuilder: IdBuilder

    @Autowired
    private lateinit var spotItemSvc: SpotItemSvc

    @Autowired
    private lateinit var itemOrderSvc: ItemOrderSvc

    @Autowired
    private lateinit var scenicSpotDao:ScenicSpotDao

    @Transactional(rollbackFor = [Exception::class])
    @Synchronized
    override fun buy(order: BuyItemOrder): BuyItemResult {
        if (order.partner == null) {
            return BuyItemResult("BUY:2002", "商户未设置")
        }
        var totalMoney = 0.0
        var totalCount = 0
        val subOrders = ArrayList<SpotItemSubOrder>()
        val orderId = idBuilder.newId("ITEM")
        for (bt in order.items) {
            val price = spotItemSvc.getSpotItemPrice(bt.itemPriceId) ?: return BuyItemResult("BUY:2002", "购买的项目价格不存在")
            val item = spotItemSvc.getSpotItem(price.itemId) ?: return BuyItemResult("BUY:2003", "购买的项目不存在")
            val scenic = scenicSpotDao.get(item.scenicSpotId) ?: return BuyItemResult("BUY:2004", "景点不存在")

            totalMoney += price.price * bt.itemNums
            totalCount += bt.itemNums
            val subOrder = SpotItemSubOrder()
            subOrder.orderId = orderId
            subOrder.itemId = item.id
            subOrder.itemPid = price.id
            subOrder.price = price.price * bt.itemNums
            subOrder.unitPrice = price.price
            subOrder.useDate = Utils.intToDate(bt.date)
            subOrder.nums = bt.itemNums
            subOrder.perNums = item.personalNums
            subOrder.createTime = Date()
            subOrder.used = 0
            subOrder.scenicId = item.scenicId
            subOrder.scenicSpotId = item.scenicSpotId
            subOrders.add(subOrder)
        }
        val order = SpotItemOrder()
        order.orderId = orderId
        order.totalPrice = totalMoney
        order.createTime = Date()
        order.nums = subOrders.size
        order.channelId= order.channelId
        order.channelUid = order.channelUid
        order.buyType = order.buyType
        val ok = itemOrderSvc.create(order, subOrders)
        if (ok) {
            val result = BuyItemResult(RESULT_SUCCESS, "ok")
            result.order = order
            return result
        }
        return BuyItemResult("fail", "创建订单失败")
    }
}