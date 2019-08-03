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
        Result(column = "not_dates", property = "notDates")
    )
    fun get(id: Int): TicketUseDate?


    @Insert(
        "insert into ticket_usedate(name,remark,work_day,weekend_day,legal_day,custom_dates,validity,not_dates) " +
                "values(#{name},#{remark},#{workDay},#{weekendDay},#{legalDay},#{customDates},#{validity},#{notDates})"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insert(uDate:TicketUseDate) : Long

    @Update(
        "update ticket_usedate set name=#{name},remark=#{remark},work_day=#{workDay},weekend_day=#{weekendDay},legal_day=#{legalDay},custom_dates=#{customDates},validity=#{validity},not_dates=#{notDates} " +
                "where id=#{id}"
    )
    fun update(uDate:TicketUseDate) : Long
}