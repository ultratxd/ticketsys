package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.Holiday
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface HolidayDao {

    @Select(
        "select * from holidays where d >= #{d}"
    )
    fun gets(d:Int): List<Holiday>

    @Insert(
        "<script>INSERT INTO holidays(d,remark) " +
                " VALUES" +
                " <foreach collection =\"hds\" item=\"hd\" separator =\",\">" +
                "   (#{hd.d}, #{hd.remark})" +
                " </foreach></script>"
    )
    fun buildInsert(hds:List<Holiday>):Long
}