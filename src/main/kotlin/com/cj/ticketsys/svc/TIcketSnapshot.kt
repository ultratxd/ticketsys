package com.cj.ticketsys.svc

import com.cj.ticketsys.entities.*
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*
import kotlin.collections.ArrayList

class TicketSnapshot {
    var id:Int = 0
    var name: String = ""
    var title: String? = null
    @JsonProperty("per_nums")
    var perNums: Int = 0
    @JsonProperty("create_time")
    var createTime: Date? = null
    @JsonProperty("enter_remark")
    var enterRemark: String = ""
    @JsonProperty("buy_remark")
    var buyRemark: String = ""
    var stocks: Int = 0
    var state: TicketStates = TicketStates.Enabled
    var cid: Int = 0
    @JsonProperty("category_name")
    var categoryName:String = ""
    @JsonProperty("icon_url")
    var iconUrl: String? = null
    var tags:MutableList<String> = ArrayList()
    var prices:MutableList<TicketPriceSnapshot> = ArrayList()
}

class TicketPriceSnapshot {
    var id: Int = 0
    var tid: Int = 0
    @JsonProperty("user_date")
    var useDate:TicketUseDate? = null
    @JsonProperty("channel_type")
    var channelType: ChannelTypes = ChannelTypes.Rack
    var name: String = ""
    var price: Double = 0.0
    @JsonProperty("create_time")
    var createTime: Date = Date()
    var stocks: Int = 0
    @JsonProperty("stock_limit_type")
    var stockLimitType: TicketStockLimitTypes = TicketStockLimitTypes.All
    var state: TicketStates = TicketStates.Enabled
    @JsonProperty("rufund_type")
    var refundType: RefundTypes = RefundTypes.NoAllow

    var title:String? = null
    var remark:String? = null
    var description:String? = null
    @JsonProperty("notice_remark")
    var noticeRemark:String? = null
}
