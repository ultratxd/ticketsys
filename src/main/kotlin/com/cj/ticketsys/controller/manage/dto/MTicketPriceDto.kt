package com.cj.ticketsys.controller.manage.dto

import com.cj.ticketsys.controller.dto.TicketUseDateDto
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class MTicketPriceDto(val id: Int) {
    @JsonProperty("ticket_id")
    var tid: Int = 0
    @JsonProperty("use_date")
    var useDate: TicketUseDateDto? = null
    @JsonProperty("channel_type")
    var channelType: Short? = null
    var name: String = ""
    var price: Double = 0.0
    @JsonProperty("original_price")
    var originalPrice: Double = 0.0
    var stocks: Int = 0
    @JsonProperty("sold_count")
    var soldCount: Int = 0
    var state: Short? = null
    @JsonProperty("refund_type")
    var refundType: Short? = null
    @JsonProperty("title")
    var title: String? = null
    @JsonProperty("notice_remark")
    var noticeRemark: String? = null
    @JsonProperty("remark")
    var remark: String? = null
    @JsonProperty("front_view")
    var frontView: Boolean? = null
    @JsonProperty("description")
    var description: String? = null
    var extra: MutableMap<String, Any> = TreeMap()
    @JsonProperty("create_time")
    var createTime: Date? = null
    @JsonProperty("stock_limit_type")
    var stockLimitType: Short? = null
    @JsonProperty("custom_prices")
    var customPrices: String? = null
    @JsonProperty("idcard_prices")
    var idCardPrices: String? = null
    @JsonProperty("buy_limit")
    var buyLimit: Int? = null
    @JsonProperty("buy_time")
    var buyTime: Int? = null
    @JsonProperty("b2b_plu")
    var b2bPLU: String? = null
}