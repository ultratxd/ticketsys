package com.cj.ticketsys.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.collections.ArrayList

open class ScenicDto(@JsonProperty("id") val id: Int) {
    var name: String = ""
    var address: String = ""
    @JsonProperty("spot_count")
    var spotCount: Int = 0
    var spots: MutableList<ScenicSpotDto> = ArrayList()
}