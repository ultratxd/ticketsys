package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.Recommend
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Result
import org.apache.ibatis.annotations.Results
import org.apache.ibatis.annotations.Select

@Mapper
interface RecommendDao {

    @Select(
        "select * from recommends where `type`=#{type} order by displayorder desc limit top"
    )
    @Results(
        Result(column = "refid",property = "refId"),
        Result(column = "create_time",property = "createTime"),
        Result(column = "displayorder",property = "displayOrder")
    )
    fun gets(top: Int, type: Short): List<Recommend>
}