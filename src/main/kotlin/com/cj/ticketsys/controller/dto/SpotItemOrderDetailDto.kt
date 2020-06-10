package com.cj.ticketsys.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class SpotItemOrderDetailDto : Serializable {
    @JsonProperty("item")
    var item:SpotItemDto? = null
    @JsonProperty("nums")
    var nums:Int = 0
    @JsonProperty("per_nums")
    var perNums:Int = 0
    @JsonProperty("surplus")
    var surplus:Int  = 0
}