package com.cj.ticketsys.controller.b2b.ctrip

data class CtripModifyOrderBody(
    var items: List<CtripModifyOrderBodyItem?>? = listOf(),
    var otaOrderId: String? = "",
    var sequenceId: String? = "",
    var supplierOrderId: String? = ""
)

data class CtripModifyOrderBodyItem(
    var itemId: String? = "",
    var originUseEndDate: String? = "",
    var originUseStartDate: String? = "",
    var targetUseEndDate: String? = "",
    var targetUseStartDate: String? = ""
)

data class CtripModifyOrderResponseBody(
    var items: List<CtripModifyOrderResponseBodyItem?>? = listOf(),
    var supplierConfirmType: Int? = 0
)

data class CtripModifyOrderResponseBodyItem(
    var itemId: String? = ""
)