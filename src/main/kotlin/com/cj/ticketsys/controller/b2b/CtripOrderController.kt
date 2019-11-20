package com.cj.ticketsys.controller.b2b

import com.cj.ticketsys.svc.b2b.CtripRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/ota/v1/b2b/ctrip")
class CtripOrderController {
    @PostMapping("/verify_order")
    fun verifyOrder(
            @RequestBody orderRequest: CtripRequest<CtripOrderItem>,
            req: HttpServletRequest
    ) {

    }

    @PostMapping("/new_order")
    fun newOrder(
            @RequestBody orderRequest: CtripRequest<CtripOrderItem>,
            req: HttpServletRequest
    ) {

    }
}

data class CtripOrderItem(
    var confirmType: Int? = 0,
    var contacts: List<Contact?>? = listOf(),
    var coupons: List<Coupon?>? = listOf(),
    var items: List<Item?>? = listOf(),
    var otaOrderId: String? = "",
    var sequenceId: String? = ""
)

data class Contact(
    var email: String? = "",
    var intlCode: String? = "",
    var mobile: String? = "",
    var name: String? = "",
    var optionalIntlCode: String? = "",
    var optionalMobile: String? = ""
)

data class Coupon(
    var amount: Int? = 0,
    var amountCurrency: String? = "",
    var code: String? = "",
    var name: String? = "",
    var type: Int? = 0
)

data class Item(
    var PLU: String? = "",
    var adjunctions: List<Adjunction?>? = listOf(),
    var cost: Int? = 0,
    var costCurrency: String? = "",
    var deposit: Deposit? = Deposit(),
    var distributionChannel: String? = "",
    var expressDelivery: ExpressDelivery? = ExpressDelivery(),
    var itemId: String? = "",
    var lastConfirmTime: String? = "",
    var openId: String? = "",
    var passengers: List<Passenger?>? = listOf(),
    var price: Int? = 0,
    var priceCurrency: String? = "",
    var quantity: Int? = 0,
    var remark: String? = "",
    var suggestedPrice: String? = "",
    var suggestedPriceCurrency: String? = "",
    var useEndDate: String? = "",
    var useStartDate: String? = ""
)

data class Adjunction(
    var content: String? = "",
    var contentCode: String? = "",
    var name: String? = "",
    var nameCode: String? = ""
)

data class Deposit(
    var amount: Int? = 0,
    var amountCurrency: String? = "",
    var type: Int? = 0
)

data class ExpressDelivery(
    var address: String? = "",
    var city: String? = "",
    var country: String? = "",
    var district: String? = "",
    var intlCode: String? = "",
    var mobile: String? = "",
    var name: String? = "",
    var province: String? = "",
    var type: Int? = 0
)

data class Passenger(
    var ageType: String? = "",
    var birthDate: String? = "",
    var birthPlace: String? = "",
    var cardIssueCountry: String? = "",
    var cardIssueDate: String? = "",
    var cardIssuePlace: String? = "",
    var cardNo: String? = "",
    var cardType: String? = "",
    var cardValidDate: String? = "",
    var firstName: String? = "",
    var gender: String? = "",
    var height: Int? = 0,
    var intlCode: String? = "",
    var lastName: String? = "",
    var mobile: String? = "",
    var myopiaDegreeL: Int? = 0,
    var myopiaDegreeR: Int? = 0,
    var name: String? = "",
    var nationalityCode: String? = "",
    var nationalityName: String? = "",
    var shoeSize: Int? = 0,
    var weight: Int? = 0
)