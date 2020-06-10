package com.cj.ticketsys.entities.spotItem

import java.util.*

class SpotItemVerificiation {
    var id:Int = 0
    var clientOrderNo:String? = null
    var clientQrCode:String? = null
    var itemOrderId:String? = null
    var itemSubId:Int? = null
    var orderType:SpotOrderTypes = SpotOrderTypes.BUY
    var createTime:Date = Date()
    var nums:Int = 0
    var verifier:String = ""
    var scenicSpotId:Int = 0
}