package com.cj.ticketsys.controller.manage.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.util.*

class MSpotItemSubOrderDto: Serializable {
    @JsonProperty("id")
    var id:Int = 0
    @JsonProperty("order_id")
    var orderId:String? = null
    @JsonProperty("item_id")
    var itemId:Int? = null
    @JsonProperty("item_pid")
    var itemPid:Int? = null
    @JsonProperty("price")
    var price:Double? = null
    @JsonProperty("unit_price")
    var unitPrice:Double? = null
    @JsonProperty("nums")
    var nums:Int? = null
    @JsonProperty("per_nums")
    var perNums:Int? = null
    @JsonProperty("create_time")
    var createTime: Date? = null
    @JsonProperty("used")
    var used:Int? = null
    @JsonProperty("scenic_id")
    var scenicId:Int = 0
    @JsonProperty("scenic_name")
    var scenicName:String = ""
    @JsonProperty("scenic_sid")
    var scenicSpotId:Int = 0
    @JsonProperty("scenic_spot_name")
    var scenicSpotName:String = ""

    @JsonProperty("item")
    var itemM:MSpotItemDto? = null
}