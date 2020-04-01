package com.cj.ticketsys.entities.spotItem

import com.cj.ticketsys.dao.BaseQuery

class SpotItemOrderQuery: BaseQuery() {
    var scenicSid: Int? = null
    var state: Int? = null
    var startTime: String? = null
    var endTime: String? = null
}