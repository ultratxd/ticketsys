package com.cj.ticketsys.entities

import java.util.*

class CardTicket : PropertyEntity() {
    var id: Int = 0
    var orderId: String = ""
    var orderSubId: Int = 0
    var channelId: String = ""
    var channelUid: String = ""
    var toUid:String? = null
    var code:String = ""
    var cardNo: String? = null
    var entityCardNo:String? = null
    var buyTime: Date = Date()
    var activatedTime: Date? = null
    var lastActivateTime: Date = Date()
    var expireTime: Date? = null
    var bindId:Int? = null
    var dayIn:Int = 0
}