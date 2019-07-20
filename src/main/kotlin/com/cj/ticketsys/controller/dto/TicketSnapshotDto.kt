package com.cj.ticketsys.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty


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
    var prices:MutableList<TicketPriceDto> = ArrayList()
}