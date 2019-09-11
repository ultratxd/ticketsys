package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.Order
import com.cj.ticketsys.entities.OrderStates
import org.apache.ibatis.annotations.*
import java.util.*

@Mapper
interface OrderDao {

    @Insert(
        "insert into orders(order_id,create_time,pay_time,pay_no,refund_time,refund_no,price,childs,state,ip,ch_id,ch_uid,buy_type,properties) " +
                "values(#{orderId},#{createTime},#{payTime},#{payNo},#{refundTime},#{refundNo},#{price},#{childs},#{state},#{ip},#{channelId},#{channelUid},#{buyType},#{properties})"
    )
    fun insert(order: Order): Long

    @Update(
        "update orders set pay_time=#{payTime},pay_no=#{payNo},refund_time=#{refundTime},refund_no=#{refundNo},state=#{state},properties=#{properties} where order_id=#{orderId}"
    )
    fun update(order: Order): Long

    @Update(
        "update orders set state=#{state} where order_id=#{orderId}"
    )
    fun updateState(orderId: String, state: OrderStates): Long

    @Update(
        "update orders set deleted=#{del} where order_id=#{orderId}"
    )
    fun updateDel(orderId: String, del: Boolean): Long

    @Delete(
        "delete from orders where order_id=#{orderNo}"
    )
    fun delete(orderNo: String): Long

    @Select(
        "<script>select count(0) from orders where ch_uid=#{uid} and deleted=0 " +
                "<if test=\"partnerIds !=null\">and ch_id in " +
                "<foreach item=\"item\" index=\"index\" collection=\"partnerIds\" open=\"(\" separator=\",\" close=\")\">" +
                "   #{item}" +
                "</foreach>" +
                "</if> " +
                "<if test=\"state !=null\">and state=#{state}</if> " +
                "</script>"
    )
    fun getsByUidCount(uid: String, partnerIds: List<String>?, state: OrderStates?): Long

    @Select(
        "<script>select * from orders where ch_uid=#{uid} and deleted=0" +
                "<if test=\"partnerIds !=null\">and ch_id in " +
                "<foreach item=\"item\" index=\"index\" collection=\"partnerIds\" open=\"(\" separator=\",\" close=\")\">" +
                "   #{item}" +
                "</foreach>" +
                "</if> " +
                "<if test=\"state !=null\">and state=#{state}</if> " +
                " order by create_time desc limit #{offset},#{size}</script>"
    )
    fun getsByUid(uid: String, partnerIds: List<String>?, state: OrderStates?, offset: Int = 0, size: Int = 20): List<Order>

    @Select(
        "select * from orders where order_id=#{id}"
    )
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "create_time", property = "createTime"),
        Result(column = "pay_time", property = "payTime"),
        Result(column = "pay_no", property = "payNo"),
        Result(column = "refund_time", property = "refundTime"),
        Result(column = "refund_no", property = "refundNo"),
        Result(column = "ch_id", property = "channelId"),
        Result(column = "ch_uid", property = "channelUid"),
        Result(column = "buy_type", property = "buyType")
    )
    fun get(id: String): Order?

    @Select(
        "select * from orders where state=0 and create_time < #{createTime}"
    )
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "create_time", property = "createTime"),
        Result(column = "pay_time", property = "payTime"),
        Result(column = "pay_no", property = "payNo"),
        Result(column = "refund_time", property = "refundTime"),
        Result(column = "refund_no", property = "refundNo"),
        Result(column = "ch_id", property = "channelId"),
        Result(column = "ch_uid", property = "channelUid"),
        Result(column = "buy_type", property = "buyType")
    )
    fun getExpiredOrders(expirationDate: Date): List<Order>


    @Select(
        "<script>" +
                "select count(DISTINCT o.order_id) from orders o join sub_orders so on o.order_id=so.order_id " +
                "<where>"+
                "<if test=\"state !=null\"> and o.state=#{state}</if>" +
                "<if test=\"payNo !=null\"> and o.pay_no=#{payNo}</if>" +
                "<if test=\"refundNo !=null\"> and o.refund_no=#{refundNo}</if>" +
                "<if test=\"chId !=null\"> and o.ch_id=#{chId}</if>" +
                "<if test=\"chUid !=null\"> and o.ch_uid=#{chUid}</if>" +
                "<if test=\"userName !=null\"> and so.uname=#{userName}</if>" +
                "<if test=\"userCard !=null\"> and so.ucard=#{userCard}</if>" +
                "<if test=\"userMobile !=null\"> and so.umobile=#{userMobile}</if>" +
                "<if test=\"scenicId !=null\"> and so.scenic_id=#{scenicId}</if>" +
                "<if test=\"scenicSid !=null\"> and so.scenic_sid=#{scenicSid}</if>" +
                "<if test=\"ticketId !=null\"> and so.ticket_id=#{ticketId}</if>" +
                "<if test=\"cid !=null\"> and so.cid=#{cid}</if>" +
                "<if test=\"buyType !=null\"> and o.buy_type=#{buyType}</if>" +
                "</where>" +
        "</script>"
    )
    fun searchForAdminCount(query: OrderQuery):Long

    @Select(
        "<script>" +
                "select DISTINCT o.order_id,o.* from orders o join sub_orders so on o.order_id=so.order_id " +
                "<where>"+
                "<if test=\"state !=null\"> and o.state=#{state}</if>" +
                "<if test=\"payNo !=null\"> and o.pay_no=#{payNo}</if>" +
                "<if test=\"refundNo !=null\"> and o.refund_no=#{refundNo}</if>" +
                "<if test=\"chId !=null\"> and o.ch_id=#{chId}</if>" +
                "<if test=\"chUid !=null\"> and o.ch_uid=#{chUid}</if>" +
                "<if test=\"userName !=null\"> and so.uname=#{userName}</if>" +
                "<if test=\"userCard !=null\"> and so.ucard=#{userCard}</if>" +
                "<if test=\"userMobile !=null\"> and so.umobile=#{userMobile}</if>" +
                "<if test=\"scenicId !=null\"> and so.scenic_id=#{scenicId}</if>" +
                "<if test=\"scenicSid !=null\"> and so.scenic_sid=#{scenicSid}</if>" +
                "<if test=\"ticketId !=null\"> and so.ticket_id=#{ticketId}</if>" +
                "<if test=\"cid !=null\"> and so.cid=#{cid}</if>" +
                "<if test=\"buyType !=null\"> and o.buy_type=#{buyType}</if>" +
                "</where>" +
                " order by o.create_time desc" +
                " limit #{offset},#{size}" +
        "</script>"
    )
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "create_time", property = "createTime"),
        Result(column = "pay_time", property = "payTime"),
        Result(column = "pay_no", property = "payNo"),
        Result(column = "refund_time", property = "refundTime"),
        Result(column = "refund_no", property = "refundNo"),
        Result(column = "ch_id", property = "channelId"),
        Result(column = "ch_uid", property = "channelUid"),
        Result(column = "buy_type", property = "buyType")
    )
    fun searchForAdmin(query: OrderQuery):List<Order>
}