package com.cj.ticketsys.entities

import java.util.*

class OrderTicketCode : PropertyEntity(){
    var id:Int = 0
    var orderId:String = ""
    var nums:Int = 0
    var code:String = ""
    var state:TicketCodeStates = TicketCodeStates.Unused
    var createTime:Date = Date()
    /**
     * 指定使用时间
     */
    var useDate:Date = Date() //需要修改，子订单使用日期不同的问题
    /**
     * 换票时间
     */
    var usedTime:Date? = null
    var provider:OrderTicketCodeProviders = OrderTicketCodeProviders.System
    var providerNo:String = ""
}