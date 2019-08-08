package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.Recommend
import org.apache.ibatis.annotations.*

@Mapper
interface RecommendDao {

    @Select(
        "select * from recommends where `type`=#{type} order by displayorder desc limit top"
    )
    @Results(
        Result(column = "refid", property = "refId"),
        Result(column = "create_time", property = "createTime"),
        Result(column = "displayorder", property = "displayOrder")
    )
    fun gets(top: Int, type: Short): List<Recommend>

    @Select(
        "select * from recommends where refid=#{refId} and `type`=#{type}"
    )
    @Results(
        Result(column = "refid", property = "refId"),
        Result(column = "create_time", property = "createTime"),
        Result(column = "displayorder", property = "displayOrder")
    )
    fun getByRefType(refId: Int, type: Short): Recommend?

    @Insert(
        "insert into recommends(refid,`type`,create_time,displayorder,properties) " +
                "values(#{refId},#{type},#{createTime},#{displayOrder},#{properties})"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insert(recommend: Recommend): Long

    @Delete(
        "delete from recommends where id=#{id}"
    )
    fun del(id:Int):Long


    @Select(
        "select count(0) from recommends"
    )
    fun getsCountForAdmin(): Long

    @Select(
        "select * from recommends limit #{offset},#{size}"
    )
    @Results(
        Result(column = "refid", property = "refId"),
        Result(column = "create_time", property = "createTime"),
        Result(column = "displayorder", property = "displayOrder")
    )
    fun getsForAdmin(offset: Int, size: Int): List<Recommend>
}