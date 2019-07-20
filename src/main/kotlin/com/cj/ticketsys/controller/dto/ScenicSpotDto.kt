package com.cj.ticketsys.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

open class ScenicSpotDto(@JsonProperty("id") val id: Int)  {
    var pid:Int = 0
    var name:String = ""
    @JsonProperty("ticket_count")
    var ticketCount:Int = 0
}