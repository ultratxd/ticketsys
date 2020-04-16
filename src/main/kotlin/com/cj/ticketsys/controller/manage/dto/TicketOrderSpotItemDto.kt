package com.cj.ticketsys.controller.manage.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class TicketOrderSpotItemDto :Serializable{
    @JsonProperty("id")
    var id: Int = 0
    @JsonProperty("order_id")
    var orderId:String = ""
    @JsonProperty("order_subid")
    var orderSubId: Int = 0
    @JsonProperty("item_id")
    var itemId:Int = 0
    @JsonProperty("item")
    var itemM:MSpotItemDto? = null
    @JsonProperty("nums")
    var nums: Int = 0
}