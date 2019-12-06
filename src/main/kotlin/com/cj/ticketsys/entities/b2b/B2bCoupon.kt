package com.cj.ticketsys.entities.b2b

class B2bCoupon : IB2bOta {
    var id:Int = 0
    override var otaId:String = ""
    var amount: Double? = null
    var amountCurrency: String? = null
    var code: String? = null
    var name: String? = null
    var type: Int? = null
}