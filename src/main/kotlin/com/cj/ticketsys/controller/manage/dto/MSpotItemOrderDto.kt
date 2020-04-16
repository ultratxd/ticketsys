package com.cj.ticketsys.controller.manage.dto

import com.cj.ticketsys.entities.OrderStates
import com.cj.ticketsys.entities.PriceDiscountTypes
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class MSpotItemOrderDto : Serializable {
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
    var state: Int = OrderStates.Init.code()
    @JsonProperty("channel_id")
    var channelId:String = ""
    @JsonProperty("channel_uid")
    var channelUid:String = ""
    @JsonProperty("code")
    var code:String = ""
    @JsonProperty("price_discount_type")
    var priceDiscountTypes: PriceDiscountTypes = PriceDiscountTypes.Nothing

    @JsonProperty("sub_orders")
    var subOrderMS:List<MSpotItemSubOrderDto> = ArrayList()
}