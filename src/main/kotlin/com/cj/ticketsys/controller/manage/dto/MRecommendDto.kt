package com.cj.ticketsys.controller.manage.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class MRecommendDto {
    var id:Int = 0
    @JsonProperty("ref_id")
    var refid:Int = 0
    @JsonProperty("title")
    var title:String = "未知"
    var type:Short = 0
    @JsonProperty("create_time")
    var createTime: Date = Date()
    @JsonProperty("display_order")
    var displayOrder:Int = 0
}