package com.cj.ticketsys.entities.b2b.ctrip

import com.cj.ticketsys.entities.b2b.IB2bOta

class B2bCtripCoupon : IB2bOta {
    var id:Int = 0
    override var otaId:String = ""
    var amount: Double? = null
    var amountCurrency: String? = null
    var code: String? = null
    var name: String? = null
    var type: Int? = null
}