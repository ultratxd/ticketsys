package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.entities.OrderStates
import com.cj.ticketsys.entities.PriceDiscountTypes
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class SpotItemOrderDto : Serializable {
    @JsonProperty("order_id")
    var orderId:String = ""
    @JsonProperty("create_time")
    var createTime: Date? = null
    @JsonProperty("nums")
    var nums:Int? = 0
    @JsonProperty("total_price")
    var totalPrice:Double = 0.0
    @JsonProperty("pay_time")
    var payTime: Date? = null
    @JsonProperty("pay_no")
    var payNo:String? = null
    @JsonProperty("refund_time")
    var refundTime: Date? = null
    @JsonProperty("refund_no")
    var refundNo:String? = null
    @JsonProperty("state")
    var state: Short = OrderStates.Init.value
    @JsonProperty("code")
    var code: String? = null
    @JsonProperty("price_discount_type")
    var priceDiscountTypes: Short = PriceDiscountTypes.Nothing.value

    @JsonProperty("sub_orders")
    var subOrders:MutableList<SpotItemSubOrderDto> = ArrayList()
}