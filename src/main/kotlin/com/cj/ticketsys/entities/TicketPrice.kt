package com.cj.ticketsys.entities

import java.util.*

class TicketPrice : PropertyEntity() {
    var id: Int = 0
    var tid: Int = 0
    var useDateId:Int = 0
    var channelType: ChannelTypes = ChannelTypes.Rack
    var name: String = ""
    var price: Double = 0.0
    var createTime: Date = Date()
    var stocks: Int = 0
    var stockLimitType: TicketStockLimitTypes = TicketStockLimitTypes.All
    var solds: Int = 0
    var state: TicketStates = TicketStates.Enabled
    var frontView:Boolean = false
    var refundType:RefundTypes = RefundTypes.NoAllow

    var title:String?
        get() = this.getProperty("title") as String?
        set(value)= this.setProperty("title",value)
    var remark:String?
        get() = this.getProperty("remark") as String?
        set(value)= this.setProperty("remark",value)
    var description:String?
        get() = this.getProperty("description") as String?
        set(value)= this.setProperty("description",value)
    var noticeRemark:String?
        get() = this.getProperty("notice_remark") as String?
        set(value)= this.setProperty("notice_remark",value)
}