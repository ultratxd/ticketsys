package com.cj.ticketsys.entities

import java.util.*

class Partner {
    var id: String = ""
    var name: String = ""
    var channelType: ChannelTypes = ChannelTypes.Rack
    var remark: String = ""
    var createTime: Date = Date()
    var sercet: String = ""
}