package com.cj.ticketsys.entities.spotItem

import com.cj.ticketsys.entities.PropertyEntity

class TicketOfItem :PropertyEntity() {
    var ticketId:Int = 0
    var itemId:Int = 0
    var nums:Int = 0
    var perNums:Int = 0
}