package com.cj.ticketsys.controller.b2b

 class CtripCreateOrderBody {
    var confirmType: Int = 0
    var contacts: List<CtripCreateOrderContact?>? = listOf()
    var coupons: List<CtripCreateOrderCoupon?>? = listOf()
    var items: List<CtripCreateOrderBodyItem?>? = listOf()
    var otaOrderId: String = ""
    var sequenceId: String? = ""
}

class CtripCreateOrderContact {
    var email: String? = ""
    var intlCode: String? = ""
    var mobile: String? = ""
    var name: String? = ""
    var optionalIntlCode: String? = ""
    var optionalMobile: String? = ""
}

class CtripCreateOrderCoupon {
    var amount: Int? = 0
    var amountCurrency: String? = ""
    var code: String? = ""
    var name: String? = ""
    var type: Int? = 0
}

class CtripCreateOrderBodyItem {
    var PLU: String? = ""
    var adjunctions: List<CtripCreateOrderAdjunction?>? = listOf()
    var cost: Int? = 0
    var costCurrency: String? = ""
    var deposit: CtripCreateOrderDeposit? = CtripCreateOrderDeposit()
    var distributionChannel: String? = ""
    var expressDelivery: CtripCreateOrderExpressDelivery? = CtripCreateOrderExpressDelivery()
    var itemId: String? = ""
    var lastConfirmTime: String? = ""
    var openId: String? = ""
    var passengers: List<CtripCreateOrderPassenger?>? = listOf()
    var price: Int? = 0
    var priceCurrency: String? = ""
    var quantity: Int? = 0
    var remark: String? = ""
    var suggestedPrice: String? = ""
    var suggestedPriceCurrency: String? = ""
    var useEndDate: String? = ""
    var useStartDate: String? = ""
}

class CtripCreateOrderAdjunction {
    var content: String? = ""
    var contentCode: String? = ""
    var name: String? = ""
    var nameCode: String? = ""
}

class CtripCreateOrderDeposit {
    var amount: Int? = 0
    var amountCurrency: String? = ""
    var type: Int? = 0
}

class CtripCreateOrderExpressDelivery {
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

class CtripCreateOrderPassenger {
    var ageType: String? = ""
    var birthDate: String? = ""
    var birthPlace: String? = ""
    var cardIssueCountry: String? = ""
    var cardIssueDate: String? = ""
    var cardIssuePlace: String? = ""
    var cardNo: String? = ""
    var cardType: String? = ""
    var cardValidDate: String? = ""
    var firstName: String? = ""
    var gender: String? = ""
    var height: Int? = 0
    var intlCode: String? = ""
    var lastName: String? = ""
    var mobile: String? = ""
    var myopiaDegreeL: Int? = 0
    var myopiaDegreeR: Int? = 0
    var name: String? = ""
    var nationalityCode: String? = ""
    var nationalityName: String? = ""
    var shoeSize: Int? = 0
    var weight: Int? = 0
}

class CtripCreateOrderResponseBody {
    var items: List<CtripCreateOrderResponseBodyItem?>? = listOf()
    var otaOrderId: String? = ""
    var supplierConfirmType: Int? = 0
    var supplierOrderId: String? = ""
    var voucherSender: Int? = 0
    var vouchers: List<CtripCreateOrderResponseBodyVoucher?>? = listOf()
}

class CtripCreateOrderResponseBodyItem {
    var inventorys: List<CtripCreateOrderResponseBodyInventory?>? = listOf()
    var itemId: String? = ""
}

class CtripCreateOrderResponseBodyInventory {
    var quantity: Int? = 0
    var useDate: String? = ""
}

class CtripCreateOrderResponseBodyVoucher {
    var itemId: String? = ""
    var voucherCode: String? = ""
    var voucherData: String? = ""
    var voucherType: Int? = 0
}