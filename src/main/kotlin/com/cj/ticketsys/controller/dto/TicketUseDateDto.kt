package com.cj.ticketsys.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

open class TicketUseDateDto {
    var id:Int = 0
    var name:String = ""
    var remark:String = ""
    @JsonProperty("work_day")
    var workDay:Boolean = false
    @JsonProperty("weekend_day")
    var weekendDay:Boolean = false
    @JsonProperty("legal_day")
    var legalDay:Boolean = false
    @JsonProperty("custom_dates")
    var customDates:String? = null
    @JsonProperty("validity")
    var validity:Int = 0
}