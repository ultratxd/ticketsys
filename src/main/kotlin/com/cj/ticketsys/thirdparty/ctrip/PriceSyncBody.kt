package com.cj.ticketsys.thirdparty.ctrip

class PriceSyncBody : CtripBody() {
    var otaOptionId:String? = null
    var supplierOptionId:String? = null
    var prices:MutableList<PriceBody> = ArrayList()
}

class PriceBody {
    var date:String = ""
    var marketPrice:Double = 0.0
    var salePrice:Double = 0.0
    var costPrice:Double = 0.0
}