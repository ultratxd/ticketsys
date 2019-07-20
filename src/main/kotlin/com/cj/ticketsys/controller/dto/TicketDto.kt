package com.cj.ticketsys.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*
import kotlin.collections.ArrayList

class TicketDto(@JsonProperty("id") val id: Int) {
    var name: String = ""
    @JsonProperty("per_nums")
    var pernums:Int = 0
    @JsonProperty("enter_remark")
    var enterRemark: String = ""
    @JsonProperty("buy_remark")
    var buyRemark:String = ""
    var stocks: Int = 0
    @JsonProperty("sold_count")
    var soldCount: Int = 0
    var state: Short? = null
    @JsonProperty("prices")
    var prices:MutableList<TicketPriceDto> = ArrayList()
    @JsonProperty("cid")
    var cid:Int = 0
    @JsonProperty("icon_url")
    var iconUrl:String? = null
    @JsonProperty("tags")
    var tags:MutableList<String> = ArrayList()

    var extra:MutableMap<String,Any> = TreeMap()

    @JsonProperty("related_tickets")
    var relatedTickets:MutableList<TicketDto>? = null
}