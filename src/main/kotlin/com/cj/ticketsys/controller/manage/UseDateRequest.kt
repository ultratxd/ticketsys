package com.cj.ticketsys.controller.manage

import com.fasterxml.jackson.annotation.JsonProperty

class UseDateRequest {
    var name:String? = null
    var remark:String? = null
    @JsonProperty("work_day")
    var workDay:Boolean = false
    @JsonProperty("work_price")
    var workPrice:Double? = null
    @JsonProperty("weekend_day")
    var weekendDay:Boolean = false
    @JsonProperty("weekend_price")
    var weekendPrice:Double? = null
    @JsonProperty("legal_day")
    var legalDay:Boolean = false
    @JsonProperty("legal_price")
    var legalPrice:Double? = null
    @JsonProperty("custom_dates")
    var customDates:List<Int>? = null
    @JsonProperty("not_dates")
    var notDates:List<Int>? = null
}