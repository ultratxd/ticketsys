package com.cj.ticketsys.entities.spotItem

import com.cj.ticketsys.dao.BaseQuery
import com.cj.ticketsys.entities.OrderStates

class SpotItemOrderQuery: BaseQuery() {
    var state: OrderStates? = null
    var startTime: String? = null
    var endTime: String? = null

    var channelUid:String? = null
    var channelId:String? = null
}