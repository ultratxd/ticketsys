package com.cj.ticketsys.entities

import java.util.*

class ClientUser {
    var id:Int = 0
    var userName:String = ""
    var pwd:String = ""
    var createTime:Date = Date()
    var remark:String = ""
    var serialNo:String = ""
    var enabled:Boolean = true
    var scenicSid:Int = 0
}

class ClientLoginLog {
    var id:Int = 0
    var uid :Int = 0
    var ip:String = ""
    var loginTime:Date = Date()
}