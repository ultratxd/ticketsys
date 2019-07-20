package com.cj.ticketsys.svc

import com.cj.ticketsys.entities.ChannelTypes
import com.cj.ticketsys.entities.Ticket
import com.cj.ticketsys.entities.TicketPrice
import java.util.*

interface PriceBinder {
    /**
     * 获取渠道某天价格
     */
    fun getPrices(ticketId: Int, channelType: ChannelTypes, date: Date): Collection<TicketPrice>
    /**
     * 票价计算器
     */
    fun bind(ticket: Ticket, channelType: ChannelTypes, date: Date): Collection<TicketPrice>
}