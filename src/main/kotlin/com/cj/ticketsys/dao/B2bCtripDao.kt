package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.b2b.B2bCtripAdjunction
import com.cj.ticketsys.entities.b2b.B2bCtripItem
import com.cj.ticketsys.entities.b2b.B2bCtripPassenger
import com.cj.ticketsys.entities.b2b.B2bCtripExpresses
import org.apache.ibatis.annotations.*
import java.util.*

@Mapper
interface B2bCtripDao {

    @Insert("insert into b2b_ctrip_expresses(ota_id,item_id,express_type,name,mobile,inil_code,country,province,city,district,address) " +
            "values(#{otaId},#{itemId},#{type},#{name},#{mobile},#{intlCode},#{country},#{province},#{city},#{district},#{address})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun saveExpresses(express: B2bCtripExpresses):Long

    @Insert("insert into b2b_ctrip_adjunctions(ota_id,item_id,name,name_code,content,content_code) values(#{otaId},#{itemId},#{name},#{nameCode},#{content},#{contentCode})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun saveAdjunction(adjunction:B2bCtripAdjunction): Long

    @Insert("insert into b2b_ctrip_passengers(ota_id,item_id,name,first_name,last_name,mobile,inil_code,card_type,card_no,birth_date,age_type,gender,nationality_code,nationality_name,card_issue_place," +
            "card_issue_date,card_valid_date,card_issue_country,birth_place,height,weight,myopia_degree_left,myopia_degree_right,shoe_size)" +
            " values(#{otaId},#{itemId},#{name},#{firstName},#{lastName},#{mobile},#{intlCode},#{cardType},#{cardNo},#{birthDate},#{ageType},#{gender},#{nationalityCode},#{nationalityName},#{cardIssuePlace}," +
            "#{cardIssueDate},#{cardValidDate},#{cardIssueCountry},#{birthPlace},#{height},#{weight},#{myopiaDegreeL},#{myopiaDegreeR},#{shoeSize})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun savePassenger(passenger: B2bCtripPassenger): Long

    @Insert("insert into b2b_ctrip_items(order_id,tkt_pid,ota_id,item_id,open_id,plu,distribution_channel,use_startdate,use_enddate,last_confirm_time,remark,price,price_cur,cost,cost_cur,suggest_price,suggest," +
            "quantity,deposit_type,deposit_amount,deposit_amount_cur)" +
            " values(#{orderId},#{ticketPriceId},#{otaId},#{itemId},#{openId},#{PLU},#{distributionChannel},#{useStartDate},#{useEndDate},#{lastConfirmTime},#{remark},#{price},#{priceCurrency},#{cost},#{costCurrency},#{suggestedPrice}," +
            "#{suggestedPriceCurrency},#{quantity},#{depositType},#{depositAmount},#{depositAmountCurrency})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun saveItem(item:B2bCtripItem): Long

    @Select("select * from b2b_ctrip_items where ota_id=#{otaId} and item_id=#{itemId}")
    @Results(
            Result(column = "order_id", property = "orderId"),
            Result(column = "tkt_pid", property = "ticketPriceId"),
            Result(column = "plu", property = "PLU"),
            Result(column = "cost_cur", property = "costCurrency"),
            Result(column = "distribution_channel", property = "distributionChannel"),
            Result(column = "item_id", property = "itemId"),
            Result(column = "open_id", property = "openId"),
            Result(column = "price_cur", property = "priceCurrency"),
            Result(column = "suggest_price", property = "suggestedPrice"),
            Result(column = "suggest", property = "suggestedPriceCurrency"),
            Result(column = "use_enddate", property = "useEndDate"),
            Result(column = "use_startdate", property = "useStartDate"),
            Result(column = "deposit_type", property = "depositType"),
            Result(column = "deposit_amount", property = "depositAmount"),
            Result(column = "deposit_amount_cur", property = "depositAmountCurrency")
    )
    fun getItem(otaId:String, itemId:String): B2bCtripItem?

    @Select("select * from b2b_ctrip_items where order_id=#{orderId}")
    @Results(
            Result(column = "order_id", property = "orderId"),
            Result(column = "tkt_pid", property = "ticketPriceId"),
            Result(column = "plu", property = "PLU"),
            Result(column = "cost_cur", property = "costCurrency"),
            Result(column = "distribution_channel", property = "distributionChannel"),
            Result(column = "item_id", property = "itemId"),
            Result(column = "open_id", property = "openId"),
            Result(column = "price_cur", property = "priceCurrency"),
            Result(column = "suggest_price", property = "suggestedPrice"),
            Result(column = "suggest", property = "suggestedPriceCurrency"),
            Result(column = "use_enddate", property = "useEndDate"),
            Result(column = "use_startdate", property = "useStartDate"),
            Result(column = "deposit_type", property = "depositType"),
            Result(column = "deposit_amount", property = "depositAmount"),
            Result(column = "deposit_amount_cur", property = "depositAmountCurrency")
    )
    fun getItemsByOrderId(orderId:String): List<B2bCtripItem>

    @Update("update b2b_ctrip_items set use_startdate=#{startDate},use_enddate=#{endDate}")
    fun updateItemDate(id:Int, startDate: Date?, endDate:Date?): Long
}