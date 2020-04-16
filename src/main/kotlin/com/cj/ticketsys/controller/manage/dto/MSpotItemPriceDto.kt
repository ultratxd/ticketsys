package com.cj.ticketsys.controller.manage.dto

import com.cj.ticketsys.entities.ChannelTypes
import com.cj.ticketsys.entities.OrderStates
import com.cj.ticketsys.entities.spotItem.ItemPriceStates
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class MSpotItemPriceDto : Serializable {
    @JsonProperty("id")
    var id:Int = 0
    @JsonProperty("item_id")
    var itemId:Int = 0
    @JsonProperty("name")
    var name:String = ""
    @JsonProperty("desc")
    var desc:String = ""
    @JsonProperty("price")
    var price:Double = 0.0
    @JsonProperty("unit")
    var unit:String = ""
    @JsonProperty("channel_type")
    var channelType: Int? = null
    @JsonProperty("state")
    var state: Int? = null
    @JsonProperty("stocks")
    var stocks:Int = 0
    @JsonProperty("solds")
    var solds:Int = 0
}