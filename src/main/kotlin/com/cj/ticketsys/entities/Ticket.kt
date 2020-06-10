package com.cj.ticketsys.entities

import java.util.*
import kotlin.collections.ArrayList

class Ticket : PropertyEntity() {
    var id: Int = 0
    var cloudId:String = ""
    var name: String = ""
    var perNums: Int = 0
    var createTime: Date = Date()
    var enterRemark: String = ""
    var buyRemark: String = ""
    var stocks: Int = 0
    var solds: Int = 0
    var state: TicketStates = TicketStates.Enabled
    var frontView: Boolean = false
    var cid: Int = 0
    var displayOrder:Int = 0
    var iconUrl: String?
        get() = this.getProperty("icon_url") as String?
        set(value) = this.setProperty("icon_url", value)

    var title: String?
        get() = this.getProperty("title") as String?
        set(value) = this.setProperty("title", value)

    var openStartTime: Date?
        get() = this.getProperty("open_start_time") as Date?
        set(value) = this.setProperty("open_start_time", value)

    var openEndTime:Date?
        get() = this.getProperty("open_end_time") as Date?
        set(value) = this.setProperty("open_end_time", value)

    var buyStartDate: String?
        get() = this.getProperty("buy_start_date") as String?
        set(value) = this.setProperty("buy_start_date", value)

    var buyEndDate:String?
        get() = this.getProperty("buy_end_date") as String?
        set(value) = this.setProperty("buy_end_date", value)

    var prices:MutableList<TicketPrice> = ArrayList()
}