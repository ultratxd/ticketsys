package com.cj.ticketsys.controller.manage.dto

import com.cj.ticketsys.controller.dto.ScenicSpotDto
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class MScenicSpotDto(@JsonProperty("id") id: Int): ScenicSpotDto(id) {
    @JsonProperty("create_time")
    var createTime: Date? = null

    @JsonProperty("properties")
    var properties: Map<String,Any> = TreeMap()
}