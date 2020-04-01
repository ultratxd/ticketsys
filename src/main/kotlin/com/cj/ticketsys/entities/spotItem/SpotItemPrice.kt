package com.cj.ticketsys.entities.spotItem

import com.cj.ticketsys.entities.ChannelTypes
import com.cj.ticketsys.entities.OrderStates
import com.cj.ticketsys.entities.PropertyEntity
import java.util.*

class SpotItemPrice : PropertyEntity() {
    var id:Int = 0
    var itemId:Int = 0
    var name:String = ""
    var desc:String = ""
    var price:Double = 0.0
    var unit:String = ""
    var channelType:ChannelTypes = ChannelTypes.Rack
    var state:ItemPriceStates = ItemPriceStates.Enabled
    var stocks:Int = 0
    var solds:Int = 0
    var createTime: Date = Date()
}