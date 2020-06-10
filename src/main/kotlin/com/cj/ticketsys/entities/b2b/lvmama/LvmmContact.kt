package com.cj.ticketsys.entities.b2b.lvmama

import com.cj.ticketsys.entities.b2b.IB2bOta

class LvmmContact:IB2bOta {
    var id:Int = 0
    override var otaId: String = ""
    var idNum:String? = null
    var idType:String? = null
    var mobile:String? = null
    var name:String? = null
}