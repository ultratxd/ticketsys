package com.cj.ticketsys.controller.manage.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.util.*

class MTicketCardDto : Serializable {
    @JsonProperty("order_no")
    var orderNo: String? = null
    @JsonProperty("buy_time")
    var buyTime: Date? = null
    @JsonProperty("card_no")
    var cardNo: String? = null
    @JsonProperty("entity_card_no")
    var entityCardNo: String? = null
    @JsonProperty("ticket_name")
    var ticketName: String? = null
    @JsonProperty("activated_time")
    var activatedTime: Date? = null
    @JsonProperty("activated_mobile")
    var activatedMobile: String? = null
    @JsonProperty("activated_fullname")
    var activatedFullname: String? = null
    @JsonProperty("activated_idcard")
    var activatedIdCard: String? = null
    @JsonProperty("code")
    var code: String? = null
    @JsonProperty("last_active_time")
    var lastActiveTime: Date? = null
    @JsonProperty("is_activated")
    var isActivated: Boolean? = null
    @JsonProperty("day_in")
    var dayIn: Int = 0
}