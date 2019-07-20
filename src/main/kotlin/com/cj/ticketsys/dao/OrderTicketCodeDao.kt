package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.OrderTicketCode
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Result
import org.apache.ibatis.annotations.Results
import org.apache.ibatis.annotations.Select

@Mapper
interface OrderTicketCodeDao  {

    @Insert(
        "insert into order_ticket_code(order_id,nums,code,state,create_time,use_date,used_time,properties) " +
                "values(#{orderId},#{nums},#{code},#{state},#{createTime},#{useDate},#{usedTime},#{properties})"
    )
    fun insert(code:OrderTicketCode):Long

    @Select(
        "select * from order_ticket_code where order_id=#{orderNo}"
    )
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "create_time", property = "createTime"),
        Result(column = "use_date", property = "useDate"),
        Result(column = "used_time", property = "usedTime")
    )
    fun get(orderNo:String): OrderTicketCode?
}