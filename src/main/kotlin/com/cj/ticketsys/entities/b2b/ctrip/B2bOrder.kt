package com.cj.ticketsys.entities.b2b.ctrip

import com.cj.ticketsys.entities.PropertyEntity
import java.util.*

class B2bOrder : IB2bOta, PropertyEntity() {
    var id:Int = 0
    var ota:Int = 0
    override var otaId:String = ""
    var orderId:String = ""
    var confirmType: Int = 0
    var quantity:Int = 0
    var items:Int = 0
    var createTime:Date = Date()
}