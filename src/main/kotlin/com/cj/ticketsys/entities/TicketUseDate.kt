package com.cj.ticketsys.entities

class TicketUseDate {
    var id: Int = 0
    var name:String = ""
    var remark:String = ""
    var workDay:Boolean= false
    var workPrice:Double? = null
    var weekendDay:Boolean = false
    var weekendPrice:Double? = null
    var legalDay:Boolean = false
    var legalPrice:Double? = null
    var customDates:String = ""
    var validity:Int = 0
    var notDates:String = ""
    var enterTime: Int? = null
}