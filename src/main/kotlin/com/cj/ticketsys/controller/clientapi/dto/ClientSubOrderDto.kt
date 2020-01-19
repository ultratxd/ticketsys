package com.cj.ticketsys.controller.clientapi.dto

import java.util.*

class ClientSubOrderDto {
    var id:Int = 0
    var clientId:Int = 0
    var cloudId:String? = null
    var clientOrderNo:String = ""
    var orderType:Short = 0
    var ticketId:Int = 0
    var ticketName:String = ""
    var amount:Double = 0.0
    var unitPrice:Double = 0.0
    var nums:Int = 0
    var perNums:Int = 0
    var createTime: Date = Date()
    var prints:Int = 0
    var useDate:Int = 0
    var enterTime:Int = 0
    var clientParentId:Int = 0
}