package com.cj.ticketsys.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

open class CardTicketDto(@JsonProperty("id") val id:Int) {
    @JsonProperty("order_id")
    var orderId: String = ""
    @JsonProperty("order_sid")
    var orderSid: Int = 0
    @JsonProperty("code")
    var code:String = ""
    @JsonProperty("card_no")
    var cardNo: String? = null
    @JsonProperty("buy_time")
    var buyTime: Date = Date()
    @JsonProperty("activated_time")
    var activatedTime: Date? = null
    @JsonProperty("last_activate_time")
    var lastActivateTime: Date = Date()
    @JsonProperty("expire_time")
    var expireTime: Date? = null
    var state:Short = 0 //0待激活,1已激活
    @JsonProperty("ticket_snapshot")
    var ticketSnapshot:TicketSnapshotDto? = null

    @JsonProperty("entity_card_no")
    var entityCardNo:String? = null

    @JsonProperty("qr_code")
    var qrCode:String? = null
}