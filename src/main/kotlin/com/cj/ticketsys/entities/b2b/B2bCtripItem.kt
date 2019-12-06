package com.cj.ticketsys.entities.b2b

import java.util.*

class B2bCtripItem : IB2bOta {
    var id:Int = 0
    var orderId:String = ""
    var ticketPriceId:Int = 0
    override var otaId: String = ""
    var PLU: String? = ""
    var cost: Double? = null
    var costCurrency: String? = null
    var distributionChannel: String? = null
    var itemId: String? = null
    var lastConfirmTime: Date? = null
    var openId: String? = null
    var price: Double? = null
    var priceCurrency: String? = null
    var quantity: Int? = null
    var remark: String? = null
    var suggestedPrice: Double? = null
    var suggestedPriceCurrency: String? = null
    var useEndDate: Date? = null
    var useStartDate: Date? = null
    var depositType:Int? = null
    var depositAmount:Double? = null
    var depositAmountCurrency:String? = null
}