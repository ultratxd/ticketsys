package com.cj.ticketsys.controller.clientapi

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

open class SubOrderReqBody {
    @JsonProperty("client_id")
    var clientId:Int = 0
    @JsonProperty("cloud_id")
    var cloudId:String? = null
    @JsonProperty("client_order_no")
    var clientOrderNo:String = ""
    @JsonProperty("order_type")
    var orderType:Short = 0
    @JsonProperty("ticket_id")
    var ticketId:Int = 0
    @JsonProperty("ticket_name")
    var ticketName:String = ""
    @JsonProperty("amount")
    var amount:Double = 0.0
    @JsonProperty("unit_price")
    var unitPrice:Double = 0.0
    @JsonProperty("nums")
    var nums:Int = 0
    @JsonProperty("per_nums")
    var perNums:Int = 0
    @JsonProperty("create_time")
    var createTime: Date = Date()
    @JsonProperty("prints")
    var prints:Int = 0
    @JsonProperty("use_date")
    var useDate:Int = 0
    @JsonProperty("enter_time")
    var enterTime:Int = 0
    @JsonProperty("client_parentId")
    var clientParentId:Int = 0
    @JsonProperty("properties")
    var properties:String? = "";
}