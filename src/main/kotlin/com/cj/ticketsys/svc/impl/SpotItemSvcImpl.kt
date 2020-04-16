package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.dao.SpotItemDao
import com.cj.ticketsys.entities.ChannelTypes
import com.cj.ticketsys.entities.PagedList
import com.cj.ticketsys.entities.spotItem.*
import com.cj.ticketsys.svc.ItemOrderSvc
import com.cj.ticketsys.svc.SpotItemSvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport

@Service
class SpotItemSvcImpl : SpotItemSvc, ItemOrderSvc {

    @Autowired
    private lateinit var spotItemDao: SpotItemDao

    override fun querySpotItems(scenicSpotId: Int): List<SpotItem> {
        return spotItemDao.querySpotItems(scenicSpotId)
    }

    override fun querySpotItemsByChannel(spotId:Int, channelType: ChannelTypes): List<SpotItem> {
        return spotItemDao.querySpotItemsByChannel(spotId,channelType.value.toInt())
    }

    /**
     * 获取单个景点项目
     */
    override fun getSpotItem(id:Int): SpotItem? {
        return spotItemDao.getSpotItem(id)
    }

    /**
     * 添加景点项目
     */
    override fun addSpotItem(item:SpotItem):Boolean {
        return spotItemDao.insertSpotItem(item) > 0
    }

    /**
     * 更新景点项目
     */
    override fun updateSpotItem(item:SpotItem):Boolean {
        return spotItemDao.updateSpotItem(item) > 0
    }

    /**
     * 项目所有渠道价格
     */
    override fun querySpotItemPrices(itemId:Int):List<SpotItemPrice> {
        return spotItemDao.querySpotItemPrices(itemId)
    }

    /**
     * 获取单个项目渠道价格
     */
    override fun getSpotItemPrice(id:Int): SpotItemPrice? {
        return spotItemDao.getSpotItemPrice(id)
    }

    override fun getSpotItemPrice(itemId: Int, channelType:ChannelTypes):SpotItemPrice? {
        return spotItemDao.getSpotItemPriceByChannel(itemId,channelType.value.toInt())
    }

    /**
     * 添加项目渠道价格
     */
    override fun addSpotItemPrice(itemPrice: SpotItemPrice):Boolean {
        return spotItemDao.insertSpotItemPrice(itemPrice) > 0
    }

    /**
     * 更新项目渠道价格
     */
    override fun updateSpotItemPrice(itemPrice: SpotItemPrice):Boolean {
        return spotItemDao.updateSpotItemPrice(itemPrice) > 0
    }

    override fun queryTicketItems(tktId: Int): List<SpotItem> {
        val tktOfItems = spotItemDao.getTicketItems(tktId)
        val items = ArrayList<SpotItem>()
        for (it in tktOfItems) {
            val item = spotItemDao.getSpotItem(it.itemId)
            if (item != null) {
                items.add(item)
            }
        }
        return items
    }

    /**
     * 获取项目关联的票价
     */
    override fun queryItemOfTickets(itemPriceId:Int):List<TicketOfItem> {
        return spotItemDao.getItemOfTickets(itemPriceId)
    }

    override fun addTicketItem(tktId: Int, itemId: Int, nums: Int, perNums:Int): Boolean {
        val tktOfItem = TicketOfItem()
        tktOfItem.ticketId = tktId
        tktOfItem.itemId = itemId
        tktOfItem.nums = nums
        tktOfItem.perNums = perNums
        return spotItemDao.insertTicketItem(tktOfItem) > 0
    }

    override fun removeTicketItem(tktId: Int, itemId: Int): Boolean {
        return spotItemDao.deleteTicketItem(tktId, itemId) > 0
    }

    override fun removeAllTicketItems(tktId: Int): Boolean {
        return spotItemDao.deleteTicketItems(tktId) > 0
    }

    /**
     * 添加订单赠送的项目
     */
    override fun addOrderItems(items:List<TicketOrderItem>):Boolean {
        var c = 0
        for(item in items) {
            c += spotItemDao.insertOrderItem(item).toInt()
        }
        if(c == items.size) return true
        return false
    }

    /**
     * 获取订单赠送的项目
     */
    override fun queryOrderItems(orderId:String):List<TicketOrderItem> {
        return spotItemDao.getOrderItemsByOrderId(orderId)
    }

    /**
     * 获取子订单赠送的项目
     */
    override fun querySubOrderItems(orderSubId:Int):List<TicketOrderItem> {
        return spotItemDao.getOrderItemsBySubOrderId(orderSubId)
    }

    /**
     * 获取订单
     */
    override fun getOrder(orderId:String): SpotItemOrder? {
        return spotItemDao.getOrder(orderId)
    }

    /**
     * 查询订单
     */
    override fun queryOrders(query: SpotItemOrderQuery,page:Int,size:Int): PagedList<SpotItemOrder> {
        val offset = (page - 1)*size
        query.offset = offset
        query.size = size
        val total = spotItemDao.queryOrdersCount(query)
        val list = spotItemDao.queryOrders(query)
        return PagedList(page,size,total,list)
    }

    /**
     * 查询提取码订单
     */
    override fun getOrderByCode(code:String): SpotItemOrder? {
        return spotItemDao.getOrderByCode(code)
    }

    /**
     * 查询支付编号订单
     */
    override fun getOrderByPayNo(no:String): SpotItemOrder? {
        return spotItemDao.getOrderByPayNo(no)
    }

    /**
     * 获取订单的子订单
     */
    override fun querySubOrders(orderId:String):List<SpotItemSubOrder> {
        return spotItemDao.querySubOrderByOrderId(orderId)
    }

    /** 订单 **/
    @Transactional(rollbackFor = [Exception::class])
    override fun create(order: SpotItemOrder, subOrder: ArrayList<SpotItemSubOrder>): Boolean {
        val c = spotItemDao.insertOrder(order)
        if (c > 0) {
            for (sub in subOrder) {
                val sc = spotItemDao.insertSubOrder(sub)
                if (sc <= 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return false
                }
                spotItemDao.incrSpotItemPriceSolds(sub.itemPid, sub.nums)
            }
        } else {
            return false
        }
        return true
    }
}