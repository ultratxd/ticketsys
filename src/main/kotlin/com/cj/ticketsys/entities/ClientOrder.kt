package com.cj.ticketsys.entities

import java.util.*

open class ClientOrder: PropertyEntity() {
    var id:Int = 0
    var clientId:Int = 0
    var cloudId:String? = null
    var clientOrderNo:String = ""
    var nums:Int = 0
    var orderType:Short = 0
    var amount:Double = 0.0
    var perNums:Int = 0
    var createTime: Date = Date()
    var state:Short = 0
    var payType:Short = 0
    var realPay:Double = 0.0
    var changePay:Double = 0.0
    var shouldPay:Double = 0.0
    var exCode:String? = null
    var remark:String? = null
    var saleClientNo:String = ""
    var ext1:String? = null
    var ext2:String? = null
    var ext3:String? = null
}