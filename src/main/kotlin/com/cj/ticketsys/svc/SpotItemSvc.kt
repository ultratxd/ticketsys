package com.cj.ticketsys.svc

import com.cj.ticketsys.dao.OrderQuery
import com.cj.ticketsys.entities.PagedList
import com.cj.ticketsys.entities.spotItem.*

interface SpotItemSvc {

    /**
     * 获取景点项目
     */
    fun querySpotItems(scenicSpotId:Int):List<SpotItem>

    /**
     * 获取单个景点项目
     */
    fun getSpotItem(id:Int): SpotItem?

    /**
     * 添加景点项目
     */
    fun addSpotItem(item:SpotItem):Boolean

    /**
     * 更新景点项目
     */
    fun updateSpotItem(item:SpotItem):Boolean

    /**
     * 项目所有渠道价格
     */
    fun querySpotItemPrices(itemId:Int):List<SpotItemPrice>

    /**
     * 获取单个项目渠道价格
     */
    fun getSpotItemPrice(id:Int):SpotItemPrice?

    fun getSpotItemPrice(itemId: Int, channelType:Int):SpotItemPrice?

    /**
     * 添加项目渠道价格
     */
    fun addSpotItemPrice(itemPrice:SpotItemPrice):Boolean

    /**
     * 更新项目渠道价格
     */
    fun updateSpotItemPrice(itemPrice:SpotItemPrice):Boolean

    /**
     * 获取票价赠送项目
     */
    fun queryTicketItems(tktPriceId:Int):List<SpotItem>

    /**
     * 获取项目关联的票价
     */
    fun queryItemOfTickets(itemPriceId:Int):List<TicketOfItem>

    /**
     * 新增票价赠送项目
     */
    fun addTicketItem(tktId:Int, tktPriceId:Int, itemId:Int, nums:Int):Boolean

    /**
     * 删除票价赠送项目
     */
    fun removeTicketItem(tktPriceId: Int,itemId:Int):Boolean

    /**
     * 删除票价所有项目
     */
    fun removeAllTicketItems(tktPriceId: Int):Boolean

    /**
     * 添加订单赠送的项目
     */
    fun addOrderItems(items:List<TicketOrderItem>):Boolean

    /**
     * 获取订单赠送的项目
     */
    fun queryOrderItems(orderId:String):List<TicketOrderItem>

    /**
     * 获取子订单赠送的项目
     */
    fun querySubOrderItems(orderSubId:Int):List<TicketOrderItem>

    /**
     * 查询订单
     */
    fun queryOrders(query:SpotItemOrderQuery,page:Int,size:Int):PagedList<SpotItemOrder>

    /**
     * 获取订单
     */
    fun getOrder(orderId:String): SpotItemOrder?

    /**
     * 查询提取码订单
     */
    fun getOrderByCode(code:String):SpotItemOrder?

    /**
     * 查询支付编号订单
     */
    fun getOrderByPayNo(no:String):SpotItemOrder?

    /**
     * 获取订单的子订单
     */
    fun querySubOrders(orderId:String):List<SpotItemSubOrder>
}