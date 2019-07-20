package com.cj.ticketsys.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

class BuyTicketResp {
    @JsonProperty("order_no")
    var orderNo = ""
    @JsonProperty("expires")
    var expires = 0L
    @JsonProperty("total_money")
    var totalMoney: Double = 0.0
    @JsonProperty("ticket_count")
    var ticketCount: Int = 0
}