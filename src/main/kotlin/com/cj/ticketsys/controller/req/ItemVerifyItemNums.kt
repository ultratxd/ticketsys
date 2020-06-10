package com.cj.ticketsys.controller.req

import com.fasterxml.jackson.annotation.JsonProperty

class ItemVerifyItemReq {
    @JsonProperty("uid")
    var uid:String? = null
    @JsonProperty("code")
    var code:String? = null
    @JsonProperty("verify_items")
    var verifyItems:List<ItemVerifyItem>? = null
}

class ItemVerifyItem {
    @JsonProperty("item_id")
    var itemId:Int? = null
    @JsonProperty("nums")
    var nums:Int? = null
}