package com.cj.ticketsys.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

class BuyItemResp {
    @JsonProperty("order_no")
    var orderNo = ""
    @JsonProperty("expires")
    var expires = 0L
    @JsonProperty("total_money")
    var totalMoney: Double = 0.0
    @JsonProperty("item_count")
    var itemCount: Int = 0
}