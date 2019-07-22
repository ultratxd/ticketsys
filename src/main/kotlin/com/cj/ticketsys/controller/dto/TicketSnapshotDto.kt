package com.cj.ticketsys.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.collections.ArrayList


class TicketSnapshotDto {
    var id: Int = 0
    var name: String = ""
    @JsonProperty("per_nums")
    var perNums: Int = 0
    @JsonProperty("enter_remark")
    var enterRemark: String = ""
    @JsonProperty("buy_remark")
    var buyRemark: String = ""
    @JsonProperty("icon_url")
    var iconUrl: String? = null
    @JsonProperty("cid")
    var cid: Int = 0
    @JsonProperty("category_name")
    var categoryName:String = ""
    @JsonProperty("tags")
    var tags:MutableList<String> = ArrayList()
    var prices:MutableList<TicketPriceSnapshotDto> = ArrayList()
}

class TicketPriceSnapshotDto {
    @JsonProperty("id")
    var id: Int = 0
    @JsonProperty("ticket_id")
    var tid: Int = 0
    @JsonProperty("use_date")
    var useDate: TicketUseDateDto? = null
    @JsonProperty("channel_type")
    var channelType: Short? = null
    var name: String = ""
    var price: Double = 0.0
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
}