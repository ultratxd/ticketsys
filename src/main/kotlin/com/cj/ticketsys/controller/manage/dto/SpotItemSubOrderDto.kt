package com.cj.ticketsys.controller.manage.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.util.*

class SpotItemSubOrderDto: Serializable {
    @JsonProperty("id")
    var id:Int = 0
    @JsonProperty("order_id")
    var orderId:String? = null
    @JsonProperty("item_id")
    var itemId:Int? = null
    @JsonProperty("price")
    var price:Double? = null
    @JsonProperty("unit_price")
    var unitPrice:Double? = null
    @JsonProperty("nums")
    var nums:Int? = null
    @JsonProperty("create_time")
    var createTime: Date? = null
    @JsonProperty("used")
    var used:Int? = null

    @JsonProperty("item")
    var item:SpotItemDto? = null
}