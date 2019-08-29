package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.BuyTypes
import com.cj.ticketsys.entities.OrderStates

open class OrderQuery : BaseQuery(){
    var state: OrderStates? = null
    var payNo:String? = null
    var refundNo:String? =null
    var chId:String? = null
    var chUid:String? = null
    var userName:String? = null
    var userCard:String? =null
    var userMobile:String? =null
    var scenicId:Int? = null
    var scenicSid:Int?= null
    var ticketId:Int? = null
    var cid:Int? = null
    var buyType:BuyTypes? = null
}