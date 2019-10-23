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

    var prices:MutableList<TicketPrice> = ArrayList()
}