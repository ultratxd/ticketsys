package com.cj.ticketsys.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class SpotItemDto : Serializable {
    @JsonProperty("id")
    var id:Int = 0
    @JsonProperty("scenic_id")
    var scenicId:Int = 0
    @JsonProperty("scenic_spot_id")
    var scenicSpotId:Int = 0
    @JsonProperty("name")
    var name:String = ""
    @JsonProperty("desc1")
    var desc1:String = ""
    @JsonProperty("desc2")
    var desc2:String = ""
    @JsonProperty("per_nums")
    var perNums:Int = 0
    @JsonProperty("price")
    var price:Double = 0.0
    @JsonProperty("prices")
    var priceMS:MutableList<SpotItemPriceDto> = ArrayList()
}