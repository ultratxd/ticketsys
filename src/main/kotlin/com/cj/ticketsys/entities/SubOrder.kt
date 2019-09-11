package com.cj.ticketsys.entities

import java.util.*

class SubOrder:PropertyEntity() {
    var id:Int = 0
    var orderId:String = ""
    var createTime:Date = Date()
    var payTime:Date? = null
    var payNo:String? = null
    var refundTime:Date? = null
    var refundNo:String? = null
    var channelId:String = ""
    var channelUid:String = ""
    var uName:String = ""
    var cardType:CardTypes = CardTypes.IDCard
    var uCard:String = ""
    var uMobile:String = ""
    var scenicId:Int = 0
    var scenicSid:Int = 0
    var ticketId:Int = 0
    var ticketPid:Int = 0
    var unitPrice:Double = 0.0
    var totalPrice:Double = 0.0
    var nums:Int = 0
    var pernums:Int = 0
    var state:OrderStates = OrderStates.Init
    var useDate:Date? = null
    var lastUseDate:Date? = null
    var snapshot:String? = null
    var cid:Int = 0
    var priceDiscountType:PriceDiscountTypes = PriceDiscountTypes.Nothing

    var issueTicketTime:Date?
        get() = this.getProperty("issue_ticket_time") as Date?
        set(value)= this.setProperty("issue_ticket_time",value)
}