package com.cj.ticketsys.controller.manage.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class TicketOfItemDto : Serializable {
    @JsonProperty("ticket_id")
    var ticketId:Int = 0
    @JsonProperty("ticket")
    var ticket: MTicketDto? = null
    @JsonProperty("item_id")
    var itemId:Int = 0
    @JsonProperty("nums")
    var nums:Int = 0
}