package com.cj.ticketsys.controller.clientapi

import com.fasterxml.jackson.annotation.JsonFormat
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
    @JsonFormat(pattern="yyyy/M/d H:m:s",timezone="GMT+8")
    var scanDate: Date? = null
    @JsonProperty("scan_time")
    var scanTime:Int = 0
    @JsonProperty("in_time")
    @JsonFormat(pattern="yyyy/M/d H:m:s",timezone="GMT+8")
    var inTime: Date? = null
    @JsonProperty("out_time")
    @JsonFormat(pattern="yyyy/M/d H:m:s",timezone="GMT+8")
    var outTime: Date? = null
    @JsonProperty("per_nums")
    var perNums:Int = 0
    @JsonProperty("in_passes")
    var inPasses:Int = 0
    @JsonProperty("out_passes")
    var outPasses:Int = 0
    @JsonProperty("properties")
    var properties:String? = ""
    @JsonProperty("scenic_spot_id")
    var scenicSpotId:Int = 0
}