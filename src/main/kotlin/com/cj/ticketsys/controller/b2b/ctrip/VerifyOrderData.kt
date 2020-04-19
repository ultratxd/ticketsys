package com.cj.ticketsys.controller.b2b.ctrip

/**
 * 订单校验Body
 */
data class CtripVerifyOrderResponseBody (
        var items:List<CtripVerifyOrderResponseBodyItem>
)

//data class CtripCreateOrder

/**
 * 订单校验Body->Item
 */
data class CtripVerifyOrderResponseBodyItem(
        var PLU:String = "",
        var inventorys: List<CtripVerifyOrderResponseBodyItemInventory>
)

/**
 * 订单校验Body->Item->Inventory
 */
data class CtripVerifyOrderResponseBodyItemInventory(
        var useDate:String = "",
        var quantity:Int = 0
)