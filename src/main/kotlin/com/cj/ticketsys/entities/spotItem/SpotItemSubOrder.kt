package com.cj.ticketsys.entities.spotItem

import com.cj.ticketsys.entities.PropertyEntity
import java.util.*

class SpotItemSubOrder: PropertyEntity() {
    var id:Int = 0
    var orderId:String = ""
    var itemId:Int = 0
    var itemPid:Int = 0
    var price:Double = 0.0
    var unitPrice:Double = 0.0
    var useDate:Date? = null
    var nums:Int = 0
    var perNums:Int = 0
    var createTime: Date = Date()
    var used:Int = 0
    var scenicId:Int = 0
    var scenicSpotId:Int = 0
}