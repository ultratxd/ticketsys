package com.cj.ticketsys.controller.manage.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class MTicketDto(val id: Int) {
    @JsonProperty("cloud_id")
    var cloudId: String = ""
    var name: String = ""
    @JsonProperty("per_nums")
    var pernums: Int = 0
    @JsonProperty("enter_remark")
    var enterRemark: String = ""
    @JsonProperty("buy_remark")
    var buyRemark: String = ""
    var stocks: Int = 0
    @JsonProperty("sold_count")
    var soldCount: Int = 0
    var state: Short? = null
    @JsonProperty("prices")
    var prices: MutableList<MTicketPriceDto> = ArrayList()
    @JsonProperty("cid")
    var cid: Int = 0
    @JsonProperty("front_view")
    var frontView: Boolean = false
    @JsonProperty("display_order")
    var displayOrder: Int = 0
    @JsonProperty("icon_url")
    var iconUrl: String? = null
    @JsonProperty("title")
    var title: String? = null
    @JsonProperty("tags")
    var tags: MutableList<String> = ArrayList()

    var extra: MutableMap<String, Any> = TreeMap()

    @JsonProperty("related_tickets")
    var relatedTickets: MutableList<MTicketDto>? = null
    @JsonProperty("create_time")
    var createTime: Date? = null

    @JsonProperty("related_spots")
    var relatedSpots: MutableList<MScenicSpotDto> = ArrayList()

    @JsonProperty("open_start_ts")
    var openStartTs:Long? = null

    @JsonProperty("open_end_ts")
    var openEndTs:Long? = null
}