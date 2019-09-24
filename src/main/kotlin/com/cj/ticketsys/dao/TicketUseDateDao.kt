package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.TicketUseDate
import org.apache.ibatis.annotations.*

@Mapper
interface TicketUseDateDao {
    @Select("select * from ticket_usedate where id=#{id}")
    @Results(
        Result(column = "work_day", property = "workDay"),
        Result(column = "weekend_day", property = "weekendDay"),
        Result(column = "legal_day", property = "legalDay"),
        Result(column = "custom_dates", property = "customDates"),
        Result(column = "not_dates", property = "notDates"),
        Result(column = "work_price", property = "workPrice"),
        Result(column = "weekend_price", property = "weekendPrice"),
        Result(column = "legal_price", property = "legalPrice"),
        Result(column = "enter_time", property = "enterTime")
    )
    fun get(id: Int): TicketUseDate?


    @Insert(
        "insert into ticket_usedate(name,remark,work_day,work_price,weekend_day,weekend_price,legal_day,legal_price,custom_dates,validity,not_dates,enter_time) " +
                "values(#{name},#{remark},#{workDay},#{workPrice},#{weekendDay},#{weekendPrice},#{legalDay},#{legalPrice},#{customDates},#{validity},#{notDates},#{enterTime})"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insert(uDate:TicketUseDate) : Long

    @Update(
        "update ticket_usedate set name=#{name},remark=#{remark},work_day=#{workDay},work_price=#{workPrice}," +
                "weekend_day=#{weekendDay},weekend_price=#{weekendPrice},legal_day=#{legalDay},legal_price=#{legalPrice}," +
                "custom_dates=#{customDates},validity=#{validity},not_dates=#{notDates},enter_time=#{enterTime} " +
                "where id=#{id}"
    )
    fun update(uDate:TicketUseDate) : Long
}