package com.cj.ticketsys.entities.spotItem

import com.cj.ticketsys.entities.PropertyEntity

class TicketOrderItem:PropertyEntity() {
    var id:Int = 0
    var orderId:String = ""
    var orderSubId:Int = 0
    var itemId:Int = 0
    var nums:Int = 0
}