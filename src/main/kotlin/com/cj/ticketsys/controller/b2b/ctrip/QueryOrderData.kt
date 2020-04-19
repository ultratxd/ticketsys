package com.cj.ticketsys.controller.b2b.ctrip

data class CtripQueryOrderRequestBody(
    var otaOrderId: String? = "",
    var sequenceId: String? = "",
    var supplierOrderId: String? = ""
)

data class CtripQueryOrderResponseBody(
    var items: List<CtripQueryOrderResponseBodyItem?>? = listOf(),
    var otaOrderId: String? = "",
    var supplierOrderId: String? = ""
)

data class CtripQueryOrderResponseBodyItem(
    var cancelQuantity: Int? = 0,
    var itemId: String? = "",
    var orderStatus: Int? = 0,
    var quantity: Int? = 0,
    var useEndDate: String? = "",
    var useQuantity: Int? = 0,
    var useStartDate: String? = ""
)