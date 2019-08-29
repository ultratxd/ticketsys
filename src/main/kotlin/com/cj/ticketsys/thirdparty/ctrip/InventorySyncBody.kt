package com.cj.ticketsys.thirdparty.ctrip

class InventorySyncBody : CtripBody() {
    var otaOptionId:String? = null
    var supplierOptionId:String? = null
    var inventorys:MutableList<InventoryBody> = ArrayList()
}

class InventoryBody {
    var date:String = ""
    var quantity:Int = 0
}