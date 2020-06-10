package com.cj.ticketsys.entities.b2b.ctrip

import com.cj.ticketsys.entities.b2b.IB2bOta

class B2bCtripAdjunction : IB2bOta {
    var id:Int = 0
    override var otaId:String = ""
    var itemId:String = ""
    var content: String? = null
    var contentCode: String? = null
    var name: String? = null
    var nameCode: String? = null
}