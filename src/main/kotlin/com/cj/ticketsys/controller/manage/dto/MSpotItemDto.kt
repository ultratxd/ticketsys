package com.cj.ticketsys.controller.manage.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class MSpotItemDto : Serializable {
    @JsonProperty("id")
    var id:Int = 0
    @JsonProperty("scenic_id")
    var scenicId:Int = 0
    @JsonProperty("scenic_spot_id")
    var scenicSpotId:Int = 0
    @JsonProperty("scenic_spot_name")
    var scenicSpotName:String? = null
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
    @JsonProperty("enabled")
    var enabled:Boolean = true
    @JsonProperty("prices")
    var priceMS:List<MSpotItemPriceDto> = ArrayList()
}