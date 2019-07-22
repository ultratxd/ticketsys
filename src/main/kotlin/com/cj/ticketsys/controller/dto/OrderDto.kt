package com.cj.ticketsys.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*
import kotlin.collections.ArrayList

class OrderDto(@JsonProperty("order_no") val no:String) {
    @JsonProperty("create_time")
    var createTime: Date = Date()
    @JsonProperty("pay_time")
    var payTime:Date? = null
    @JsonProperty("pay_no")
    var payNo:String? = null
    @JsonProperty("refund_time")
    var refundTime:Date? = null
    @JsonProperty("refund_no")
    var refundNo:String? = null
    @JsonProperty("price")
    var price:Double = 0.0
    @JsonProperty("childs")
    var childs:Int = 0
    @JsonProperty("state")
    var state:Short = 0
    @JsonProperty("sub_orders")
    var subOrders:MutableList<SubOrderDto> = ArrayList()
    var extra:MutableMap<String,Any> = TreeMap()

    @JsonProperty("expire_seconds")
    var expireSeconds:Int? = null
}