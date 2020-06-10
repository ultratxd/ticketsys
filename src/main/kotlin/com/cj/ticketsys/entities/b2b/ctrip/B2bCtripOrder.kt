package com.cj.ticketsys.entities.b2b.ctrip

import com.cj.ticketsys.entities.PropertyEntity
import com.cj.ticketsys.entities.b2b.IB2bOta
import java.util.*

class B2bCtripOrder : IB2bOta, PropertyEntity() {
    var id:Int = 0
    override var otaId:String = ""
    var orderId:String = ""
    var confirmType: Int = 0
    var quantity:Int = 0
    var items:Int = 0
    var createTime:Date = Date()
}