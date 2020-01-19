package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.b2b.B2bContact
import com.cj.ticketsys.entities.b2b.B2bCoupon
import com.cj.ticketsys.entities.b2b.B2bOrder
import org.apache.ibatis.annotations.*

@Mapper
interface B2bDao {

    @Insert("insert into b2b_orders(ota,ota_id,order_id,confirm_type,quantity,items,create_time,properties) values(#{ota},#{otaId},#{orderId},#{confirmType},#{quantity},#{items},#{createTime},#{properties})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun saveOrder(order: B2bOrder): Long

    @Select("select * from b2b_orders where ota_id=#{otaId} and ota=#{ota}")
    @Results(
            Result(column = "ota_id", property = "otaId"),
            Result(column = "order_id", property = "orderId"),
            Result(column = "confirm_type", property = "confirmType"),
            Result(column = "create_ime", property = "createTime")
    )
    fun getOrderByOtaId(otaId:String,ota:Int): B2bOrder?

    @Select("select * from b2b_orders where order_id=#{orderId} and ota=#{ota}")
    @Results(
            Result(column = "ota_id", property = "otaId"),
            Result(column = "order_id", property = "orderId"),
            Result(column = "confirm_type", property = "confirmType"),
            Result(column = "create_ime", property = "createTime")
    )
    fun getOrder(orderId:String,ota:Int): B2bOrder?

    @Insert("insert into b2b_contacts(ota_id,name,mobile,intl_code,opt_mobile,opt_intl_code,email) " +
            "values(#{otaId},#{name},#{mobile},#{intlCode},#{optionalMobile},#{optionalIntlCode},#{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun saveContact(contact:B2bContact):Long

    @Insert("insert into b2b_coupons(ota_id,coup_type,code,name,amount,amount_currency) " +
            "values(#{otaId},#{type},#{code},#{name},#{amount},#{amountCurrency})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun saveCoupon(contact:B2bCoupon):Long
}