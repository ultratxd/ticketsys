package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.Order
import com.cj.ticketsys.entities.OrderStates
import org.apache.ibatis.annotations.*
import java.util.*

@Mapper
interface OrderDao {

    @Insert(
        "insert into orders(order_id,create_time,pay_time,pay_no,refund_time,refund_no,price,childs,state,ip,ch_id,ch_uid,properties) " +
                "values(#{orderId},#{createTime},#{payTime},#{payNo},#{refundTime},#{refundNo},#{price},#{childs},#{state},#{ip},#{channelId},#{channelUid},#{properties})"
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
        "select count(0) from orders where ch_uid=#{uid} and ch_id=#{partnerId}"
    )
    fun getsByUidCount(uid: String, partnerId: String): Long

    @Select(
        "<script>select * from orders where ch_uid=#{uid} and ch_id=#{partnerId} <if test=\"state !=null\">and state=#{state}</if> and deleted=0 order by create_time desc limit #{offset},#{size}</script>"
    )
    fun getsByUid(uid: String, partnerId: String, state: OrderStates?, offset: Int = 0, size: Int = 20): List<Order>

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
        Result(column = "ch_uid", property = "channelUid")
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
        Result(column = "ch_uid", property = "channelUid")
    )
    fun getExpiredOrders(expirationDate: Date): List<Order>
}