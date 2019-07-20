package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.Partner
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface PartnerDao {

    @Select("select * from partner where id=#{id}")
    fun get(id:String):Partner?
}