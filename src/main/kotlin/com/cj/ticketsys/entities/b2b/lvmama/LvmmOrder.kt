package com.cj.ticketsys.entities.b2b.lvmama

import com.cj.ticketsys.entities.b2b.IB2bOta
import java.util.*

class LvmmOrder :IB2bOta {
    override var otaId:String = ""
    var orderId:String = ""
    var uid:String? = null
    var ts:String? = null
    var visitTs:String? = null
    var goodId:String? = null
    var settlePrice:Double? = null
    var nums:Int? = null
    var createTime: Date = Date()
}