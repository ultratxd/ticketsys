package com.cj.ticketsys.controller.clientapi

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class OrderReqBody {
    @JsonProperty("client_id")
    var clientId:Int = 0
    @JsonProperty("cloud_id")
    var cloudId:String? = null
    @JsonProperty("client_order_no")
    var clientOrderNo:String = ""
    @JsonProperty("nums")
    var nums:Int = 0
    @JsonProperty("order_type")
    var orderType:Short = 0
    @JsonProperty("amount")
    var amount:Double = 0.0
    @JsonProperty("per_nums")
    var perNums:Int = 0
    @JsonProperty("create_time")
    var createTime: Date = Date()
    @JsonProperty("state")
    var state:Short = 0
    @JsonProperty("pay_type")
    var payType:Short = 0
    @JsonProperty("real_pay")
    var realPay:Double = 0.0
    @JsonProperty("change_pay")
    var changePay:Double = 0.0
    @JsonProperty("should_pay")
    var shouldPay:Double = 0.0
    @JsonProperty("ex_code")
    var exCode:String? = null
    @JsonProperty("remark")
    var remark:String? = null
    @JsonProperty("sale_client_no")
    var saleClientNo:String = ""
    @JsonProperty("ex1")
    var ext1:String? = null
    @JsonProperty("ex2")
    var ext2:String? = null
    @JsonProperty("ex3")
    var ext3:String? = null
}