package com.cj.ticketsys.controller.b2b.ctrip

data class CtripRefundOrderBody(
    var items: List<CtripRefundOrderBodyItem?>? = listOf(),
    var lastConfirmTime: String? = "",
    var otaOrderId: String? = "",
    var sequenceId: String? = "",
    var supplierOrderId: String? = "",
    var totalAmount: Int? = 0,
    var totalAmountCurrency: String? = ""
)

data class CtripRefundOrderBodyItem(
    var PLU: String? = "",
    var amount: Int? = 0,
    var amountCurrency: String? = "",
    var itemId: String? = ""
)

data class CtripRefundOrderResponseBody (
    var supplierConfirmType: Int? = 0
)