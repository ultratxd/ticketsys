package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.CardTicket
import org.apache.ibatis.annotations.*

@Mapper
interface CardTicketDao {

    @Insert(
        "insert into card_tkts(order_id,order_sid,ch_id,ch_uid,code,card_no,buy_time,activated_time,last_activate_time,expire_time,properties)" +
                " values(#{orderId},#{orderSubId},#{channelId},#{channelUid},#{code},#{cardNo},#{buyTime},#{activatedTime},#{lastActivateTime},#{expireTime},#{properties})"
    )
    fun insert(ct: CardTicket): Long

    @Update(
        "update card_tkts set card_no=#{cardNo},activated_time=#{activatedTime},properties=#{properties}"
    )
    fun update(ct: CardTicket): Long

    @Select(
        "select * from card_tkts where id=#{id}"
    )
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "order_sid", property = "orderSubId"),
        Result(column = "ch_id", property = "channelId"),
        Result(column = "ch_uid", property = "channelUid"),
        Result(column = "card_no", property = "cardNo"),
        Result(column = "buy_time", property = "buyTime"),
        Result(column = "activated_time", property = "activatedTime"),
        Result(column = "last_activate_time", property = "lastActivateTime"),
        Result(column = "expire_time", property = "expireTime")
    )
    fun get(id: Int): CardTicket

    @Select(
        "select * from card_tkts where code=#{code}"
    )
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "order_sid", property = "orderSubId"),
        Result(column = "ch_id", property = "channelId"),
        Result(column = "ch_uid", property = "channelUid"),
        Result(column = "card_no", property = "cardNo"),
        Result(column = "buy_time", property = "buyTime"),
        Result(column = "activated_time", property = "activatedTime"),
        Result(column = "last_activate_time", property = "lastActivateTime"),
        Result(column = "expire_time", property = "expireTime")
    )
    fun getByCode(code: String): CardTicket

    @Select(
        "select * from card_tkts where ch_id=#{channelId} and ch_uid=#{channelUid}"
    )
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "order_sid", property = "orderSubId"),
        Result(column = "ch_id", property = "channelId"),
        Result(column = "ch_uid", property = "channelUid"),
        Result(column = "card_no", property = "cardNo"),
        Result(column = "buy_time", property = "buyTime"),
        Result(column = "activated_time", property = "activatedTime"),
        Result(column = "last_activate_time", property = "lastActivateTime"),
        Result(column = "expire_time", property = "expireTime")
    )
    fun getsByPartner(channelId: String, channelUid: String): List<CardTicket>
}