package com.cj.ticketsys.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class TicketPriceDto(@JsonProperty("id") val id: Int) {
    @JsonProperty("ticket_id")
    var tid: Int = 0
    @JsonProperty("use_date")
    var useDate: TicketUseDateDto? = null
    @JsonProperty("channel_type")
    var channelType: Short? = null
    var name: String = ""
    var price: Double = 0.0
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
    @JsonProperty("description")
    var description: String? = null
    var extra:MutableMap<String,Any> = TreeMap()
}