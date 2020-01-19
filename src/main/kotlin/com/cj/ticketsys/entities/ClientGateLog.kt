package com.cj.ticketsys.entities

import java.util.*

class ClientGateLog:PropertyEntity() {
    var id:Int = 0
    var clientId:Int = 0
    var clientOrderNo:String = ""
    var clientOrderSid:Int = 0
    var code:String = ""
    var cType:Short = 0
    var scanDate:Date? = null
    var scanTime:Int = 0
    var inTime:Date? = null
    var outTime:Date? = null
    var perNums:Int = 0
    var inPasses:Int = 0
    var outPasses:Int = 0
}