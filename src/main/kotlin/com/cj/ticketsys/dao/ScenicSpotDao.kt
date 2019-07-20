package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.ScenicSpot
import org.apache.ibatis.annotations.*

@Mapper
interface ScenicSpotDao {
    @Select("select * from scenic_spot where pid=#{pid}")
    @Results(
        Result(column = "create_time", property = "createTime")
    )
    fun gets(pid: Int): List<ScenicSpot>

    @Select("select * from scenic_spot where id=#{id}")
    @Results(
        Result(column = "create_time", property = "createTime")
    )
    fun get(id: Int): ScenicSpot?

    @Insert(
        "insert into scenic_spot(pid,`name`,tickets,create_time,properties) " +
                "values(#{pid},#{name},#{tickets},#{createTime},#{properties})"
    )
    fun insert(spot: ScenicSpot): Long


    @Update(
        "update scenic_spot set pid=#{pid},`name`=#{name},create_time=#{createTime},properties=#{properties} where id=#{id}"
    )
    fun update(spot: ScenicSpot): Long

    @Update(
        "update scenic_spot set tickets=tickets+#{count} where id=#{id}"
    )
    fun updateTicketCount(id: Int, count: Int): Long
}