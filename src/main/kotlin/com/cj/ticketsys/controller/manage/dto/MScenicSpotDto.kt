package com.cj.ticketsys.controller.manage.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class MScenicSpotDto(@JsonProperty("id") id: Int) {
    var pid:Int = 0
    var name:String = ""
    @JsonProperty("ticket_count")
    var ticketCount:Int = 0
    @JsonProperty("create_time")
    var createTime: Date? = null
    @JsonProperty("properties")
    var properties: Map<String,Any> = TreeMap()
}