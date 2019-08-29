package com.cj.ticketsys.controller.manage.dto

import com.cj.ticketsys.controller.dto.TicketSnapshotDto
import com.cj.ticketsys.entities.PriceDiscountTypes
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class MSubOrderDto(val id: Int,@JsonProperty("order_no") val orderNo: String) {
    @JsonProperty("create_time")
    var createTime: Date = Date()
    @JsonProperty("pay_time")
    var payTime: Date? = null
    @JsonProperty("pay_no")
    var payNo:String? = null
    @JsonProperty("refund_time")
    var refundTime: Date? = null
    @JsonProperty("refund_no")
    var refundNo: Date? = null
    @JsonProperty("uid")
    var uid:String = ""
    @JsonProperty("scenic_id")
    var scenicId:Int = 0
    @JsonProperty("scenic_sid")
    var scenicSid:Int = 0
    @JsonProperty("ticket_id")
    var ticketId:Int = 0
    @JsonProperty("unit_price")
    var unitPrice:Double = 0.0
    @JsonProperty("total_price")
    var totalPrice:Double = 0.0
    var nums:Int = 0
    @JsonProperty("pernums")
    var pernums:Int = 0
    var state:Short = 0
    @JsonProperty("use_date")
    var useDate: Date? = null
    @JsonProperty("last_use_date")
    var lastUseDate: Date? = null
    @JsonProperty("card_type")
    var cardType:Short = 0
    @JsonProperty("user_card")
    var userCard:String = ""
    @JsonProperty("user_mobile")
    var userMobile:String = ""
    @JsonProperty("user_name")
    var userName:String = ""
    @JsonProperty("cid")
    var cid:Int = 0
    @JsonProperty("category_name")
    var categoryName:String? = ""
    @JsonProperty("price_discount_type")
    var priceDiscountType:Short = PriceDiscountTypes.Nothing.value

    var extra:MutableMap<String,Any> = TreeMap()

    @JsonProperty("ticket_snapshot")
    var ticketSnapshot: TicketSnapshotDto? = null
}