package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.b2b.lvmama.LvmmContact
import com.cj.ticketsys.entities.b2b.lvmama.LvmmOrder
import com.cj.ticketsys.entities.b2b.lvmama.LvmmTraveller
import org.apache.ibatis.annotations.*

@Mapper
interface B2bLvmmDao {

    @Insert("insert into b2b_lvmm_orders(serial_no,order_no,uid,ts,vts,good_id,settle_price,nums,create_time) " +
            "values(#{otaId},#{orderId},#{uid},#{ts},#{vts},#{goodId},#{settlePrice},#{nums},#{createTime})")
    fun insertOrder(order:LvmmOrder):Long

    @Update(
        "update b2b_lvmm_orders set status=#{status} where serial_no=#{otaId}"
    )
    fun updateOrder(order:LvmmOrder):Long

    @Select(
        "select * from b2b_lvmm_orders where serial_no=#{otaId}"
    )
    @Results(
        Result(column = "serial_no", property = "otaId"),
        Result(column = "order_no", property = "orderId"),
        Result(column = "vts", property = "visitTs")
    )
    fun getOrder(otaId:String): LvmmOrder?

    @Insert("insert into b2b_lvmm_contacts(serial_no,id_num,id_type,mobile,name) values(#{otaId},#{idNum},#{idType},#{mobile},#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insertContact(contact: LvmmContact):Long

    @Select("select * from b2b_lvmm_contacts where serial_no=#{otaId}")
    @Results(
        Result(column = "serial_no", property = "otaId")
    )
    fun getContacts(otaId:String):List<LvmmContact>

    @Insert("insert into b2b_lvmm_travellers(serial_no,id_num,id_type,mobile,name) values(#{otaId},#{idNum},#{idType},#{mobile},#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insertTraveller(traveller: LvmmTraveller):Long

    @Select("select * from b2b_lvmm_travellers where serial_no=#{otaId}")
    @Results(
        Result(column = "serial_no", property = "otaId")
    )
    fun getTravellers(otaId:String):List<LvmmTraveller>
}