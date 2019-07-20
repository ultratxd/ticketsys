package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.TicketUseDate
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Result
import org.apache.ibatis.annotations.Results
import org.apache.ibatis.annotations.Select

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
}