package com.cj.ticketsys.controller.clientapi

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class GateLogReqBody {
    @JsonProperty("client_id")
    var clientId:Int = 0
    @JsonProperty("client_order_no")
    var clientOrderNo:String = ""
    @JsonProperty("client_order_sid")
    var clientOrderSid:Int = 0
    @JsonProperty("code")
    var code:String = ""
    @JsonProperty("ctype")
    var cType:Short = 0
    @JsonProperty("scan_date")
    var scanDate: Date = Date()
    @JsonProperty("scan_time")
    var scanTime:Int = 0
    @JsonProperty("in_time")
    var inTime: Date? = null
    @JsonProperty("out_time")
    var outTime: Date? = null
    @JsonProperty("per_nums")
    var perNums:Int = 0
    @JsonProperty("in_passes")
    var inPasses:Int = 0
    @JsonProperty("out_passes")
    var outPasses:Int = 0

    @JsonProperty("properties")
    var properties:String? = "";
}