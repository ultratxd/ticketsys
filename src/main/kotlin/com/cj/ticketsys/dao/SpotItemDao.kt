package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.spotItem.*
import org.apache.ibatis.annotations.*

@Mapper
interface SpotItemDao {

    /**
     * spotItem dao
     */
    @Select("select * from spot_items where id=#{id}")
    @Results(
        Result(column = "scenic_id", property = "scenicId"),
        Result(column = "scenic_sid", property = "scenicSpotId"),
        Result(column = "desc1", property = "desc1"),
        Result(column = "desc2", property = "desc2"),
        Result(column = "per_nums", property = "personalNums"),
        Result(column = "create_time", property = "createTime")
    )
    fun getSpotItem(id:Int): SpotItem?

    @Select("select * from spot_items where scenic_sid=#{spotId}")
    @Results(
        Result(column = "scenic_id", property = "scenicId"),
        Result(column = "scenic_sid", property = "scenicSpotId"),
        Result(column = "desc1", property = "desc1"),
        Result(column = "desc2", property = "desc2"),
        Result(column = "per_nums", property = "personalNums"),
        Result(column = "create_time", property = "createTime")
    )
    fun querySpotItems(spotId:Int): List<SpotItem>

    @Select("select si.* from spot_items si join spot_item_prices sip on si.id=sip.item_id" +
            " where si.scenic_sid=#{spotId} and sip.channel_type=#{channelType}")
    @Results(
        Result(column = "scenic_id", property = "scenicId"),
        Result(column = "scenic_sid", property = "scenicSpotId"),
        Result(column = "desc1", property = "desc1"),
        Result(column = "desc2", property = "desc2"),
        Result(column = "per_nums", property = "personalNums"),
        Result(column = "create_time", property = "createTime")
    )
    fun querySpotItemsByChannel(spotId:Int, channelType: Int): List<SpotItem>

    @Insert("insert into spot_items(scenic_id,scenic_sid,name,desc1,desc2,per_nums,price,create_time,enabled,properties) " +
            "values(#{scenicId},#{scenicSpotId},#{name},#{desc1},#{desc2},#{personalNums},#{price},#{createTime},#{enabled},#{properties})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insertSpotItem(item:SpotItem):Long

    @Update(
        "update spot_items set scenic_id=#{scenicId},scenic_sid=#{scenicSpotId},name=#{name},desc1=#{desc1},desc2=#{desc2}," +
                "per_nums=#{personalNums},price=#{price},enabled=#{enabled},properties=#{properties} where id=#{id}"
    )
    fun updateSpotItem(item:SpotItem):Long

    /**
     * spotItemPrice
     */
    @Select("select * from spot_item_prices where id=#{id}")
    @Results(
        Result(column = "item_id", property = "itemId"),
        Result(column = "channel_type", property = "channelType")
    )
    fun getSpotItemPrice(id:Int):SpotItemPrice?

    @Select("select * from spot_item_prices where item_id=#{itemId}")
    @Results(
        Result(column = "item_id", property = "itemId"),
        Result(column = "channel_type", property = "channelType")
    )
    fun querySpotItemPrices(itemId:Int): List<SpotItemPrice>

    @Insert("insert into spot_item_prices(item_id,`name`,`desc`,price,`unit`,channel_type,state,stocks,solds,create_time,properties) " +
            "values(#{itemId},#{name},#{desc},#{price},#{unit},#{channelType},#{state},#{stocks},#{solds},#{createTime},#{properties})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insertSpotItemPrice(price:SpotItemPrice):Long

    @Update(
        "update spot_item_prices set `name`=#{name},`desc`=#{desc},price=#{price},`unit`=#{unit},state=#{state}," +
                "stocks=#{stocks},properties=#{properties} where id=#{id}"
    )
    fun updateSpotItemPrice(price: SpotItemPrice):Long

    @Select("select * from spot_item_prices where item_id=#{itemId} and channel_type=#{channelType}")
    @Results(
        Result(column = "item_id", property = "itemId"),
        Result(column = "channel_type", property = "channelType")
    )
    fun getSpotItemPriceByChannel(itemId:Int, channelType:Int):SpotItemPrice?

    @Update(
        "update spot_item_prices set solds=solds+#{sells} where id=#{id}"
    )
    fun incrSpotItemPriceSolds(id:Int,sells:Int):Long

    /**
     * spotItemOrder
     */
    @Select("select * from spot_item_orders where order_id=#{orderId}")
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "ch_id", property = "channelId"),
        Result(column = "ch_uid", property = "channelUid"),
        Result(column = "price_discount_type", property = "priceDiscountTypes")
    )
    fun getOrder(orderId:String):SpotItemOrder?

    @Insert("insert into spot_item_orders(order_id,create_time,nums,total_price,pay_time,pay_no,refund_time,refund_no,state,ch_id,ch_uid,code,buy_type,price_discount_type,properties) " +
            "values(#{orderId},#{createTime},#{nums},#{totalPrice},#{payTime},#{payNo},#{refundTime},#{refundNo},#{state},#{channelId},#{channelUid},#{code},#{buyType},#{priceDiscountTypes},#{properties})")
    fun insertOrder(order:SpotItemOrder):Long

    @Update(
        "update spot_item_orders set nums=#{nums},total_price=#{totalPrice},pay_time=#{payTime}," +
                "pay_no=#{payNo},refund_time=#{refundTime}," +
                "refund_no=#{refundNo},state=#{state},ch_id=#{channelId},ch_uid=#{channelUid}," +
                "code=#{code},price_discount_type=#{priceDiscountTypes}," +
                "properties=#{properties} where order_id=#{orderId}"
    )
    fun updateOrder(order:SpotItemOrder):Long

    @Select("<script>select * from spot_item_orders " +
            "<where>" +
            "<if test=\"state !=null\">and state=#{state}</if>" +
            "<if test=\"startTime !=null\">and create_time > #{startTime}</if>" +
            "<if test=\"endTime !=null\">and create_time &lt; #{endTime}</if>" +
            "<if test=\"channelUid !=null\">and ch_uid = #{channelUid}</if>" +
            "<if test=\"channelId !=null\">and ch_id = #{channelId}</if>" +
            "</where>" +
            "limit #{offset},#{size}" +
            "</script>")
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "ch_id", property = "channelId"),
        Result(column = "ch_uid", property = "channelUid"),
        Result(column = "price_discount_type", property = "priceDiscountTypes")
    )
    fun queryOrders(query:SpotItemOrderQuery):List<SpotItemOrder>

    @Select("<script>select count(0) from spot_item_orders " +
            "<where>" +
            "<if test=\"state !=null\">and state=#{state}</if>" +
            "<if test=\"startTime !=null\">and create_time > #{startTime}</if>" +
            "<if test=\"endTime !=null\">and create_time &lt; #{endTime}</if>" +
            "<if test=\"channelUid !=null\">and ch_uid = #{channelUid}</if>" +
            "<if test=\"channelId !=null\">and ch_id = #{channelId}</if>" +
            "</where>" +
            "</script>")
    fun queryOrdersCount(query:SpotItemOrderQuery): Long

    @Select("select * from spot_item_orders where pay_no=#{payNo}")
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "ch_id", property = "channelId"),
        Result(column = "ch_uid", property = "channelUid"),
        Result(column = "price_discount_type", property = "priceDiscountTypes")
    )
    fun getOrderByPayNo(payNo:String): SpotItemOrder?

    @Select("select * from spot_item_orders where code=#{code}")
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "ch_id", property = "channelId"),
        Result(column = "ch_uid", property = "channelUid"),
        Result(column = "price_discount_type", property = "priceDiscountTypes")
    )
    fun getOrderByCode(code:String):SpotItemOrder?

    /**
     * spotItemSubOrder
     */
    @Select("select * from spot_item_suborders where id=#{id}")
    @Results(
        Result(column = "scenic_sid", property = "scenicSpotId")
    )
    fun getSubOrder(orderId:String):SpotItemSubOrder?

    @Insert("insert into spot_item_suborders(order_id,item_id,item_pid,price,unit_price,use_date,nums,per_nums,create_time,used,scenic_id,scenic_sid,properties) " +
            "values(#{orderId},#{itemId},#{itemPid},#{price},#{unitPrice},#{useDate},#{nums},#{perNums},#{createTime},#{used},#{scenicId},#{scenicSpotId},#{properties})")
    fun insertSubOrder(subOrder: SpotItemSubOrder):Long

    @Update(
        "update spot_item_suborders set used=#{used},properties=#{properties} where id=#{id}"
    )
    fun updateSubOrder(subOrder:SpotItemSubOrder):Long

    @Select("select * from spot_item_suborders where order_id=#{orderId}")
    @Results(
        Result(column = "scenic_sid", property = "scenicSpotId")
    )
    fun querySubOrderByOrderId(orderId:String):List<SpotItemSubOrder>


    /**
     * spotItemVerification
     */
    @Select("select * from spot_item_verifications where tkt_order_id=#{ticketOrderId}")
    @Results(
        Result(column = "tkt_order_id", property = "ticketOrderId"),
        Result(column = "item_order_id", property = "itemOrderId"),
        Result(column = "scenic_id", property = "scenicSpotId")
    )
    fun getVerificationByTkt(ticketOrderId:String):SpotItemVerificiation?

    @Select("select * from spot_item_verifications where item_order_id=#{itemOrderId}")
    @Results(
        Result(column = "tkt_order_id", property = "ticketOrderId"),
        Result(column = "item_order_id", property = "itemOrderId"),
        Result(column = "scenic_id", property = "scenicSpotId")
    )
    fun getVerificationByItem(itemOrderId:String):SpotItemVerificiation

    @Insert("insert into spot_item_verifications(tkt_order_id,item_order_id,order_type,create_time,nums,verifier,scenic_sid,properties) " +
            "values(#{ticketOrderId},#{itemOrderId},#{orderType},#{createTime},#{nums},#{verifier},#{scenicSpotId},#{properties})")
    fun insertVerification(spotItemVerificiation: SpotItemVerificiation) :Long

//    @Update(
//        "update spot_item_verifications "
//    )
//    fun updateVerification(spotItemVerificiation: SpotItemVerificiation):Long

    /**
     * TktSpotItem
     */
    @Select("select * from ticket_spot_items where tkt_id=#{tktId}")
    @Results(
        Result(column = "tkt_id", property = "ticketId"),
        Result(column = "tkt_pid", property = "ticketPriceId"),
        Result(column = "item_id", property = "itemId"),
        Result(column = "item_pid", property = "itemPriceId")
    )
    fun getTicketItems(tktId:Int):List<TicketOfItem>

    @Select("select * from ticket_spot_items where item_pid=#{itemPriceId}")
    @Results(
        Result(column = "tkt_id", property = "ticketId"),
        Result(column = "tkt_pid", property = "ticketPriceId"),
        Result(column = "item_id", property = "itemId"),
        Result(column = "item_pid", property = "itemPriceId")
    )
    fun getItemOfTickets(itemPriceId:Int):List<TicketOfItem>

    @Insert("insert into ticket_spot_items(tkt_id,item_pid,item_id,nums,properties) values(#{ticketId},#{itemPriceId},#{itemId},#{nums},#{properties})")
    fun insertTicketItem(ticketItem:TicketOfItem):Long

    @Update("update ticket_spot_items set nums=nums+#{nums} where tkt_id=#{tktId} and itemId=#{itemId}")
    fun updateTicketItemNums(tktId:Int,itemId:Int,nums:Int):Long

    @Delete("delete from ticket_spot_items where tkt_id=#{tktId} and item_id=#{itemId}")
    fun deleteTicketItem(tktId:Int,itemId:Int):Long

    @Delete("delete from ticket_spot_items where tkt_id=#{tktId}")
    fun deleteTicketItems(tktId:Int):Long

    /**
     * OrderSpotItem
     */
    @Select("select * from order_spot_items where order_sid=#{orderSubId}")
    @Results(
        Result(column = "order_sid", property = "orderSubId")
    )
    fun getOrderItemsBySubOrderId(orderSubId:Int):List<TicketOrderItem>

    @Select("select * from order_spot_items where order_id=#{orderId}")
    @Results(
        Result(column = "order_sid", property = "orderSubId")
    )
    fun getOrderItemsByOrderId(orderId:String):List<TicketOrderItem>

    @Insert("insert into order_spot_items(order_id,order_sid,item_id,nums,properties) values(#{orderId},#{orderSubId},#{itemId},#{nums},#{properties})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insertOrderItem(orderItem:TicketOrderItem):Long

    @Update("update order_spot_items set nums=nums+#{nums} where id=#{id}")
    fun updateOrderItemNums(id:Int,nums:Int):Long
}