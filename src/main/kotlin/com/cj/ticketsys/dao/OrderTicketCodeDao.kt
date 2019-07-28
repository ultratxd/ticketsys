package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.OrderTicketCode
import com.cj.ticketsys.entities.OrderTicketCodeProviders
import com.cj.ticketsys.entities.TicketCodeStates
import org.apache.ibatis.annotations.*
import java.util.*

@Mapper
interface OrderTicketCodeDao {

    @Insert(
        "insert into order_ticket_code(order_id,nums,code,state,create_time,use_date,used_time,provider,provider_no,properties) " +
                "values(#{orderId},#{nums},#{code},#{state},#{createTime},#{useDate},#{usedTime},#{provider},#{providerNo},#{properties})"
    )
    fun insert(code: OrderTicketCode): Long

    @Select(
        "select * from order_ticket_code where order_id=#{orderNo}"
    )
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "create_time", property = "createTime"),
        Result(column = "use_date", property = "useDate"),
        Result(column = "used_time", property = "usedTime"),
        Result(column = "provider_no", property = "providerNo")
    )
    fun get(orderNo: String): OrderTicketCode?

    @Select(
        "select * from order_ticket_code where code=#{code} and provider=#{provider}"
    )
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "create_time", property = "createTime"),
        Result(column = "use_date", property = "useDate"),
        Result(column = "used_time", property = "usedTime"),
        Result(column = "provider_no", property = "providerNo")
    )
    fun getByCode(code: String, provider: OrderTicketCodeProviders): OrderTicketCode?

    @Update(
        "update order_ticket_code set used_time=#{userTime},state=#{state} where order_id=#{orderNo}"
    )
    fun update(orderNo: String, userTime: Date, state: TicketCodeStates): Long
}