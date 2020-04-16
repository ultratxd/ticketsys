package com.cj.ticketsys.svc

import com.cj.ticketsys.entities.BuyTypes
import com.cj.ticketsys.entities.Partner
import com.cj.ticketsys.entities.spotItem.SpotItemOrder

interface ItemBuyer {
    fun buy(order: BuyItemOrder): BuyItemResult
}

class BuyItemOrder {
    var partner: Partner? = null
    var channelUid: String = ""
    var buyerIp: String = ""
    var buyType: BuyTypes = BuyTypes.Online
    var items: MutableList<BuyItemInfo> = ArrayList()
}

class BuyItemInfo {
    var itemPriceId: Int = 0
    var itemNums: Int = 0
    var date: Int = 0
//    var userName: String = ""
//    var cardType: CardTypes = CardTypes.IDCard
//    var userCard: String = ""
//    var userMobile: String = ""
}

class BuyItemResult(val status: String, val msg: String) {
    var order: SpotItemOrder? = null
}