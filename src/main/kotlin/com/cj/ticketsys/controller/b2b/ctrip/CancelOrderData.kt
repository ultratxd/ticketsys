package com.cj.ticketsys.controller.b2b.ctrip

data class CancelOrderBody(
    var confirmType: Int? = 0,
    var items: List<CancelOrderBodyItem?>? = listOf(),
    var otaOrderId: String? = "",
    var sequenceId: String? = "",
    var supplierOrderId: String? = ""
)

data class CancelOrderBodyItem(
    var PLU: String? = "",
    var amount: Int? = 0,
    var amountCurrency: String? = "",
    var itemId: String? = "",
    var lastConfirmTime: String? = "",
    var quantity: Int? = 0
)

data class CtripCancelOrderResponseBody(
    var items: List<CtripCancelOrderResponse?>? = listOf(),
    var supplierConfirmType: Int? = 0
)

data class CtripCancelOrderResponse(
    var itemId: String? = ""
)