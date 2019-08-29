package com.cj.ticketsys.controller.req

import com.fasterxml.jackson.annotation.JsonProperty

class BuyRequest {
    @JsonProperty("partner_id")
    var partnerId: String = ""
    @JsonProperty("uid")
    var channelUid: String = ""
    @JsonProperty("buyer_ip")
    var buyerIp: String = ""
    @JsonProperty("scenic_sid")
    var scenicSid: Int = 0
    @JsonProperty("buy_type", required = false)
    var buyType: Short = 0
    @JsonProperty("tickets")
    var BuyTickets: List<BuyTicket> = ArrayList()
}

class BuyTicket {
    @JsonProperty("ticket_pid")
    var ticketPriceId: Int = 0
    @JsonProperty("ticket_nums")
    var ticketNums: Int = 0
    var date: Int = 0
    @JsonProperty("user_name")
    var userName: String = ""
    @JsonProperty("card_type")
    var cardType: Int = 0
    @JsonProperty("user_card")
    var userCard: String = ""
    @JsonProperty("user_mobile")
    var userMobile: String = ""
}