package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.Scenic
import org.apache.ibatis.annotations.*

@Mapper
interface ScenicDao {

    @Select("select * from scenic")
    @Results(
        Result(column = "addr", property = "address"),
        Result(column = "create_time", property = "createTime")
    )
    fun gets():List<Scenic>

    @Select("select * from scenic where id=#{id}")
    @Results(
        Result(column = "addr", property = "address"),
        Result(column = "create_time", property = "createTime")
    )
    fun get(id:Int):Scenic?

    @Insert(
        "insert into scenic(`name`,addr,create_time,spots,properties) " +
                "values(#{name},#{address},#{createTime},#{spots},#{properties})"
    )
    fun insert(scenic: Scenic):Long


    @Update(
        "update scenic set `name`=#{name},addr=#{address},properties=#{properties} where id=#{id}"
    )
    fun update(scenic:Scenic):Long

    @Update(
        "update scenic set spots=spots+#{count} where id=#{id}"
    )
    fun updateSpotCount(id: Int, count: Int): Long
}