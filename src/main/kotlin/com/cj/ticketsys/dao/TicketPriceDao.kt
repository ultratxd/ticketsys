package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.TicketPrice
import org.apache.ibatis.annotations.*

@Mapper
interface TicketPriceDao {
    @Select(
        "select * from ticket_price where id=#{id}"
    )
    @Results(
        Result(column = "usedate_id", property = "useDateId"),
        Result(column = "channel_type", property = "channelType"),
        Result(column = "create_time", property = "createTime"),
        Result(column = "stock_limit_type", property = "stockLimitType"),
        Result(column = "front_view", property = "frontView"),
        Result(column = "refund_type", property = "refundType"),
        Result(column = "plu", property = "b2bPLU")
    )
    fun get(id: Int): TicketPrice?

    @Select(
        "select * from ticket_price where tid=#{tid}"
    )
    @Results(
        Result(column = "usedate_id", property = "useDateId"),
        Result(column = "channel_type", property = "channelType"),
        Result(column = "create_time", property = "createTime"),
        Result(column = "stock_limit_type", property = "stockLimitType"),
        Result(column = "front_view", property = "frontView"),
        Result(column = "refund_type", property = "refundType"),
        Result(column = "plu", property = "b2bPLU")
    )
    fun gets(tid: Int): List<TicketPrice>

    @Select(
        "select * from ticket_price where state=1 and channel_type=#{channel}"
    )
    @Results(
        Result(column = "usedate_id", property = "useDateId"),
        Result(column = "channel_type", property = "channelType"),
        Result(column = "create_time", property = "createTime"),
        Result(column = "stock_limit_type", property = "stockLimitType"),
        Result(column = "front_view", property = "frontView"),
        Result(column = "refund_type", property = "refundType"),
        Result(column = "plu", property = "b2bPLU")
    )
    fun getsByChannelAndEnabled(channel: Int): List<TicketPrice>

    @Insert(
        "insert into ticket_price(tid,usedate_id,channel_type,`name`,price,create_time,stocks,stock_limit_type,state,front_view,refund_type,properties,plu) " +
                "values(#{tid},#{useDateId},#{channelType},#{name},#{price},#{createTime},#{stocks},#{stockLimitType},#{state},#{frontView},#{refundType},#{properties},#{b2bPLU})"
    )
    fun insert(price: TicketPrice): Long

    @Update(
        "update ticket_price set tid=#{tid},usedate_id=#{useDateId},channel_type=#{channelType},`name`=#{name},price=#{price},stocks=#{stocks},stock_limit_type=#{stockLimitType},state=#{state},front_view=#{frontView},refund_type=#{refundType},properties=#{properties},plu=#{b2bPLU} " +
                "where id=#{id}"
    )
    fun update(ticket: TicketPrice): Long

    @Update(
        "update ticket_price set solds=solds+#{solds} where id=#{id}"
    )
    fun updateSolds(id: Int, solds: Int): Long
}