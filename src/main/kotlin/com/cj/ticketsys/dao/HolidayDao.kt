package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.Holiday
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface HolidayDao {

    @Select(
        "select * from holidays where d >= #{d}"
    )
    fun gets(d:Int): List<Holiday>
}