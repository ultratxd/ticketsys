package com.cj.ticketsys.entities

import java.util.*

class OrderTicketCode : PropertyEntity(){
    var id:Int = 0
    var orderId:String = ""
    var nums:Int = 0
    var code:String = ""
    var state:TicketCodeStates = TicketCodeStates.Unused
    var createTime:Date = Date()
    var useDate:Date = Date()
    var usedTime:Date? = null
}