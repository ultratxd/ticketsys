package com.cj.ticketsys.entities.spotItem

import com.cj.ticketsys.entities.OrderStates
import com.cj.ticketsys.entities.PriceDiscountTypes
import com.cj.ticketsys.entities.PropertyEntity
import java.util.*

class SpotItemOrder : PropertyEntity() {
    var orderId:String = ""
    var createTime: Date = Date()
    var nums:Int = 0
    var totalPrice:Double = 0.0
    var payTime:Date? = null
    var payNo:String? = null
    var refundTime:Date? = null
    var refundNo:String? = null
    var scenicId:Int = 0
    var scenicSpotId:Int = 0
    var state:OrderStates = OrderStates.Init
    var channelId:String = ""
    var channelUid:String = ""
    var code:String = ""
    var priceDiscountTypes:PriceDiscountTypes = PriceDiscountTypes.Nothing
}