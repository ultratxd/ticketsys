package com.cj.ticketsys.entities.spotItem

import com.cj.ticketsys.entities.PropertyEntity
import java.util.*

class SpotItem :PropertyEntity() {
    var id:Int = 0
    var scenicId:Int = 0
    var scenicSpotId:Int = 0
    var name:String = ""
    var desc1:String = ""
    var desc2:String = ""
    var personalNums:Int = 0
    var price:Double = 0.0
    var createTime: Date = Date()
    var enabled:Boolean = true
}