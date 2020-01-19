package com.cj.ticketsys.controller.clientapi.dto

import java.util.*


open class ClientOrderDto {
    var id: Int = 0
    var clientId: Int = 0
    var cloudId: String? = null
    var clientOrderNo: String? = null
    var nums: Int = 0
    var orderType: Short = 0
    var amount: Double = 0.0
    var perNums: Int = 0
    var createTime: Date? = null
    var state: Short = 0
    var payType: Short = 0
    var realPay: Double = 0.0
    var changePay: Double = 0.0
    var shouldPay: Double = 0.0
    var exCode: String? = null
    var remark: String? = null
    var saleClientNo: String? = null
    var ext1: String? = null
    var ext2: String? = null
    var ext3: String? = null
    var properties: String? = ""
    var childrens: List<ClientSubOrderDto>? = null
}