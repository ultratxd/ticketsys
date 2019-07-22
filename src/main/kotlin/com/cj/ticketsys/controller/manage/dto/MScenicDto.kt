package com.cj.ticketsys.controller.manage.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class MScenicDto(@JsonProperty("id") id: Int) {
    var name: String = ""
    var address: String = ""
    @JsonProperty("spot_count")
    var spotCount: Int = 0
    @JsonProperty("create_time")
    var createTime: Date? = null
    @JsonProperty("properties")
    var properties: Map<String,Any> = TreeMap()
}