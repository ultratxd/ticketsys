package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.OrderStates
import com.cj.ticketsys.entities.SubOrder
import org.apache.ibatis.annotations.*
import java.util.*

@Mapper
interface SubOrderDao {
    @Insert(
        "insert into sub_orders(order_id,create_time,pay_time,pay_no,refund_time,refund_no,ch_id,ch_uid,uname,card_type,ucard,umobile,scenic_id,scenic_sid,ticket_id,ticket_pid,unit_price,total_price,nums,pernums,state,use_date,last_use_date,snapshot,cid,properties) " +
                "values(#{orderId},#{createTime},#{payTime},#{payNo},#{refundTime},#{refundNo},#{channelId},#{channelUid},#{uName},#{cardType},#{uCard},#{uMobile},#{scenicId},#{scenicSid},#{ticketId},#{ticketPid},#{unitPrice},#{totalPrice},#{nums},#{pernums},#{state},#{useDate},#{lastUseDate},#{snapshot},#{cid},#{properties})"
    )
    fun insert(order: SubOrder): Long

    @Update(
        "update sub_orders set pay_time=#{payTime},pay_no=#{payNo},refund_time=#{refundTime},refund_no=#{refundNo},uname=#{uName},card_type=#{cardType},ucard=#{uCard},umobile=#{uMobile},refund_no=#{refundNo},state=#{state},properties=#{properties} where id=#{id}"
    )
    fun update(order: SubOrder): Long

    @Update(
        "update sub_orders set state=#{state} where id=#{id}"
    )
    fun updateState(id: Int, state: OrderStates): Long

    @Delete(
        "delete from sub_orders where order_id=#{orderNo}"
    )
    fun delete(orderNo:String): Long

    @Update(
        "<script>update sub_orders set state=#{state} " +
                "<if test=\"payTime !=null\">,pay_time=#{payTime}</if>" +
                "<if test=\"payNo !=null\">,pay_no=#{payNo}</if>" +
                "<if test=\"refundTime !=null\">,refund_time=#{refundTime}</if>" +
                "<if test=\"refundNo !=null\">,refund_no=#{refundNo}</if>" +
                " where order_id=#{orderNo}</script>"
    )
    fun batchUpadte(
        orderNo: String,
        state: OrderStates,
        payTime: Date?,
        payNo: String?,
        refundTime: Date?,
        refundNo: String?
    ): Long

    @Select(
        "select * from sub_orders where id=#{id}"
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
        Result(column = "scenic_id", property = "scenicId"),
        Result(column = "scenic_sid", property = "scenicSid"),
        Result(column = "ticket_id", property = "ticketId"),
        Result(column = "ticket_pid", property = "ticketPid"),
        Result(column = "unit_price", property = "unitPrice"),
        Result(column = "total_price", property = "totalPrice"),
        Result(column = "pernums", property = "pernums"),
        Result(column = "use_date", property = "useDate"),
        Result(column = "last_use_date", property = "lastUseDate"),
        Result(column = "uname", property = "uName"),
        Result(column = "card_type", property = "cardType"),
        Result(column = "ucard", property = "uCard"),
        Result(column = "umobile", property = "uMobile")
    )
    fun get(id: Int): SubOrder?

    @Select(
        "select * from sub_orders where order_id=#{orderNo}"
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
        Result(column = "scenic_id", property = "scenicId"),
        Result(column = "scenic_sid", property = "scenicSid"),
        Result(column = "ticket_id", property = "ticketId"),
        Result(column = "ticket_pid", property = "ticketPid"),
        Result(column = "unit_price", property = "unitPrice"),
        Result(column = "total_price", property = "totalPrice"),
        Result(column = "pernums", property = "pernums"),
        Result(column = "use_date", property = "useDate"),
        Result(column = "last_use_date", property = "lastUseDate"),
        Result(column = "uname", property = "uName"),
        Result(column = "card_type", property = "cardType"),
        Result(column = "ucard", property = "uCard"),
        Result(column = "umobile", property = "uMobile")
    )
    fun gets(orderNo: String): List<SubOrder>
}