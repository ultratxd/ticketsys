package com.cj.ticketsys.controller.req

import com.fasterxml.jackson.annotation.JsonProperty

class ItemBuyRequest {
    @JsonProperty("partner_id")
    var partnerId: String = ""
    @JsonProperty("uid")
    var channelUid: String = ""
    @JsonProperty("buyer_ip")
    var buyerIp: String = ""
    @JsonProperty("buy_type", required = false)
    var buyType: Short = 0
    @JsonProperty("items")
    var buyItems: List<BuyItem> = ArrayList()
}

class BuyItem {
    @JsonProperty("item_pid")
    var itemPriceId: Int = 0
    @JsonProperty("item_nums")
    var itemNums: Int = 0
    var date: Int = 0
}