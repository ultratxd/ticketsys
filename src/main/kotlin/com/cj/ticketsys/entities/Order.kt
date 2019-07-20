package com.cj.ticketsys.entities

import java.util.*

class Order:PropertyEntity() {
    var orderId:String = ""
    var createTime:Date = Date()
    var payTime:Date? = null
    var payNo:String? = null
    var refundTime:Date? = null
    var refundNo:String? = null
    var price:Double = 0.0
    var childs:Int = 0
    var state:OrderStates = OrderStates.Init
    var ip:String = ""
    var channelId:String = ""
    var channelUid:String = ""

    var issueTicketTime:Date?
        get() = this.getProperty("issue_ticket_time") as Date?
        set(value)= this.setProperty("issue_ticket_time",value)
}