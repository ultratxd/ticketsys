package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.CardTicket
import com.cj.ticketsys.entities.CardTicketBindInfo
import org.apache.ibatis.annotations.*

@Mapper
interface CardTicketDao {

    @Insert(
        "insert into card_tkts(order_id,order_sid,ch_id,ch_uid,to_uid,code,card_no,entity_card_no,buy_time,activated_time,last_activate_time,expire_time,bind_id,day_in,properties)" +
                " values(#{orderId},#{orderSubId},#{channelId},#{channelUid},#{toUid},#{code},#{cardNo},#{entityCardNo},#{buyTime},#{activatedTime},#{lastActivateTime},#{expireTime},#{bindId},#{dayIn},#{properties})"
    )
    fun insert(ct: CardTicket): Long

    @Update(
        "update card_tkts set to_uid=#{toUid},card_no=#{cardNo},entity_card_no=#{entityCardNo},activated_time=#{activatedTime},bind_id=#{bindId}.day_in=#{dayIn},properties=#{properties} where id=#{id}"
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
        Result(column = "entity_card_no", property = "entityCardNo"),
        Result(column = "buy_time", property = "buyTime"),
        Result(column = "activated_time", property = "activatedTime"),
        Result(column = "last_activate_time", property = "lastActivateTime"),
        Result(column = "expire_time", property = "expireTime"),
        Result(column = "bind_id", property = "bindId")
    )
    fun get(id: Int): CardTicket?

    @Select(
        "select * from card_tkts where card_no=#{cardNo}"
    )
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "order_sid", property = "orderSubId"),
        Result(column = "ch_id", property = "channelId"),
        Result(column = "ch_uid", property = "channelUid"),
        Result(column = "card_no", property = "cardNo"),
        Result(column = "entity_card_no", property = "entityCardNo"),
        Result(column = "buy_time", property = "buyTime"),
        Result(column = "activated_time", property = "activatedTime"),
        Result(column = "last_activate_time", property = "lastActivateTime"),
        Result(column = "expire_time", property = "expireTime"),
        Result(column = "bind_id", property = "bindId")
    )
    fun getByCardNo(cardNo:String): CardTicket?

    @Select(
        "SELECT ifnull(max(card_no),0) FROM `card_tkts`"
    )
    fun getMaxCardNo():Long

    @Select(
        "select * from card_tkts where code=#{code}"
    )
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "order_sid", property = "orderSubId"),
        Result(column = "ch_id", property = "channelId"),
        Result(column = "ch_uid", property = "channelUid"),
        Result(column = "card_no", property = "cardNo"),
        Result(column = "entity_card_no", property = "entityCardNo"),
        Result(column = "buy_time", property = "buyTime"),
        Result(column = "activated_time", property = "activatedTime"),
        Result(column = "last_activate_time", property = "lastActivateTime"),
        Result(column = "expire_time", property = "expireTime"),
        Result(column = "bind_id", property = "bindId")
    )
    fun getByCode(code: String): CardTicket?

    @Select(
        "select * from card_tkts where ch_id=#{channelId} and ch_uid=#{channelUid}"
    )
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "order_sid", property = "orderSubId"),
        Result(column = "ch_id", property = "channelId"),
        Result(column = "ch_uid", property = "channelUid"),
        Result(column = "card_no", property = "cardNo"),
        Result(column = "entity_card_no", property = "entityCardNo"),
        Result(column = "buy_time", property = "buyTime"),
        Result(column = "activated_time", property = "activatedTime"),
        Result(column = "last_activate_time", property = "lastActivateTime"),
        Result(column = "expire_time", property = "expireTime"),
        Result(column = "bind_id", property = "bindId")
    )
    fun getsByPartner(channelId: String, channelUid: String): List<CardTicket>

    @Select(
        "select * from card_tkts where ch_id=#{channelId} and ch_uid=#{channelUid} and (ch_uid=to_uid or to_uid=null)"
    )
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "order_sid", property = "orderSubId"),
        Result(column = "ch_id", property = "channelId"),
        Result(column = "ch_uid", property = "channelUid"),
        Result(column = "card_no", property = "cardNo"),
        Result(column = "entity_card_no", property = "entityCardNo"),
        Result(column = "buy_time", property = "buyTime"),
        Result(column = "activated_time", property = "activatedTime"),
        Result(column = "last_activate_time", property = "lastActivateTime"),
        Result(column = "expire_time", property = "expireTime"),
        Result(column = "bind_id", property = "bindId")
    )
    fun getsSelfByPartner(channelId: String, channelUid: String): List<CardTicket>

    @Select(
        "select * from card_tkts where ch_id=#{channelId} and ch_uid=#{channelUid} and (ch_uid != to_uid and to_uid != null)"
    )
    @Results(
        Result(column = "order_id", property = "orderId"),
        Result(column = "order_sid", property = "orderSubId"),
        Result(column = "ch_id", property = "channelId"),
        Result(column = "ch_uid", property = "channelUid"),
        Result(column = "card_no", property = "cardNo"),
        Result(column = "entity_card_no", property = "entityCardNo"),
        Result(column = "buy_time", property = "buyTime"),
        Result(column = "activated_time", property = "activatedTime"),
        Result(column = "last_activate_time", property = "lastActivateTime"),
        Result(column = "expire_time", property = "expireTime"),
        Result(column = "bind_id", property = "bindId")
    )
    fun getsRelayByPartner(channelId: String, channelUid: String): List<CardTicket>


    /**
     * 绑定卡片
     */
    @Select(
        "select * from card_tkts_bind where id=#{id}"
    )
    @Results(
        Result(column = "full_name", property = "fullName"),
        Result(column = "id_card", property = "idCard"),
        Result(column = "post_time", property = "postTime")
    )
    fun getBindInfoById(id:Int): CardTicketBindInfo?

    @Select(
        "select * from card_tkts_bind where mobile=#{mobile}"
    )
    @Results(
        Result(column = "full_name", property = "fullName"),
        Result(column = "id_card", property = "idCard"),
        Result(column = "post_time", property = "postTime")
    )
    fun getBindInfoByMobile(mobile:String): List<CardTicketBindInfo>

    @Select(
        "select * from card_tkts_bind where id_card=#{idCard}"
    )
    @Results(
        Result(column = "full_name", property = "fullName"),
        Result(column = "id_card", property = "idCard"),
        Result(column = "post_time", property = "postTime")
    )
    fun getBindInfoByIdCard(idCard:String): List<CardTicketBindInfo>

    @Insert(
        "insert into card_tkts_bind(full_name,id_card,mobile,avatar,post_time,properties) " +
                "values(#{fullName},#{idCard},#{mobile},#{avatar},#{postTime},#{properties})"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insertBindInfo(bind:CardTicketBindInfo):Long

    @Update(
        "update card_tkts_bind set full_name=#{fullName},id_card=#{idCard},mobile=#{mobile},avatar=#{avatar} where id=#{id}"
    )
    fun updateBindInfo(bind:CardTicketBindInfo):Long
}