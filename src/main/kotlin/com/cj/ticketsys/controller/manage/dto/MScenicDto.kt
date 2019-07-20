package com.cj.ticketsys.controller.manage.dto

import com.cj.ticketsys.controller.dto.ScenicDto
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class MScenicDto(@JsonProperty("id") id: Int) : ScenicDto(id) {
    @JsonProperty("create_time")
    var createTime: Date? = null

    @JsonProperty("properties")
    var properties: Map<String,Any> = TreeMap()
}