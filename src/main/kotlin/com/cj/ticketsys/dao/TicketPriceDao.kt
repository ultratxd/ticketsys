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
        Result(column = "refund_type", property = "refundType")
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
        Result(column = "refund_type", property = "refundType")
    )
    fun gets(tid: Int): List<TicketPrice>

    @Insert(
        "insert into ticket_price(tid,usedate_id,channel_type,`name`,price,create_time,stocks,stock_limit_type,state,front_view,refund_type,properties) " +
                "values(#{tid},#{useDateId},#{channelType},#{perNums},#{name},#{price},#{createTime},#{stocks},#{stockLimitType},#{state},#{frontView},#{refundType},#{properties})"
    )
    fun insert(price: TicketPrice): Long

    @Update(
        "update ticket_price set tid=#{tid},usedate_id=#{useDateId},channel_type=#{channelType},pernums=#{perNums},`name`=#{name},stocks=#{stocks},stock_limit_type=#{stockLimitType},state=#{state},front_view=#{frontView},refund_type=#{refundType},properties=#{properties} " +
                "where id=#{id}"
    )
    fun update(ticket: TicketPrice): Long

    @Update(
        "update ticket_price set solds=solds+#{solds} where id=#{id}"
    )
    fun updateSolds(id: Int, solds: Int): Long
}