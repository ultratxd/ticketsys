package com.cj.ticketsys.entities.b2b

class B2bCtripExpresses : IB2bOta {
    var id:Int = 0
    override var otaId:String = ""
    var itemId:String = ""
    var address: String? = null
    var city: String? = null
    var country: String? = null
    var district: String? = null
    var intlCode: String? = null
    var mobile: String? = null
    var name: String? = null
    var province: String? = null
    var type: Int? = null
}