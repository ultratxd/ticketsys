package com.cj.ticketsys.entities.b2b.ctrip

import com.cj.ticketsys.entities.b2b.IB2bOta

class B2bContact : IB2bOta {
    var id:Int = 0
    override var otaId:String = ""
    var email: String? = null
    var intlCode: String? = null
    var mobile: String? = null
    var name: String? = null
    var optionalIntlCode: String? = null
    var optionalMobile: String? = null
}