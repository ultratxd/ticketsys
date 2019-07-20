package com.cj.ticketsys.svc

import com.cj.ticketsys.entities.CardTypes
import com.cj.ticketsys.entities.Order
import com.cj.ticketsys.entities.Partner


interface TicketBuyer {
    fun buy(order: BuyTicketOrder): BuyResult
}

class BuyTicketOrder {
    var partner: Partner? = null
    var channelUid: String = ""
    var buyerIp:String = ""
    var scenicSpotId = 0
    var buyTickets: MutableList<BuyTicketInfo> = ArrayList()
}

class BuyTicketInfo {
    var ticketPriceId: Int = 0
    var ticketNums: Int = 0
    var date: Int = 0
    var userName:String = ""
    var cardType:CardTypes = CardTypes.IDCard
    var userCard: String = ""
    var userMobile: String = ""
}

class BuyResult(val status: String, val msg: String) {
    var order: Order? = null
}