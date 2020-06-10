package com.cj.ticketsys.entities

import java.util.*

class CardTicketBindInfo : PropertyEntity() {
    var id:Int = 0
    var fullName:String? = null
    var idCard:String? = null
    var mobile:String? = null
    var avatar:String? = null
    var postTime:Date = Date()
}