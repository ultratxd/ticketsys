package com.cj.ticketsys.controller.manage

import com.fasterxml.jackson.annotation.JsonProperty

class UseDateRequest {
    var name:String? = null
    var remark:String? = null
    @JsonProperty("work_day")
    var workDay:Boolean = false
    @JsonProperty("weekend_day")
    var weekendDay:Boolean = false
    @JsonProperty("legal_day")
    var legalDay:Boolean = false
    @JsonProperty("custom_dates")
    var customDates:List<String>? = null
    @JsonProperty("not_dates")
    var notDates:List<String>? = null
}